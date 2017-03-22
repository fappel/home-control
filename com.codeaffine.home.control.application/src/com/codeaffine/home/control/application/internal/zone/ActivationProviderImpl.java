package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.Messages.ZONE_ACTIVATION_STATUS_CHANGED_INFO;
import static com.codeaffine.home.control.application.internal.zone.ZoneUtil.markForInPathRelease;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorEvent;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusProviderCore;

public class ActivationProviderImpl implements ActivationProvider {

  static final long IN_PATH_RELEASES_EXPIRATION_TIME = 10L;
  static final long RELEASE_TIMEOUT_INTERVAL = 20L;
  static final long PATH_EXPIRED_TIMEOUT = 60L;

  private final ExpiredInPathReleaseSkimmer expiredInPathReleaseSkimmer;
  private final SensorReferenceTracker sensorReferenceTracker;
  private final ExpiredPathsSkimmer expiredPathsSkimmer;
  private final StatusProviderCore<Activation> core;
  private final PathAdjacency adjacency;
  private final ZoneUtil zoneUtil;
  private final Set<Path> paths;

  public ActivationProviderImpl( AdjacencyDefinition adjacencyDefinition, EventBus eventBus, Logger logger ) {
    verifyNotNull( adjacencyDefinition, "adjacencyDefinition" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.paths = new HashSet<>();
    this.core = new StatusProviderCore<>( eventBus, new Activation( emptySet() ), this, logger );
    this.expiredInPathReleaseSkimmer = new ExpiredInPathReleaseSkimmer( paths );
    this.sensorReferenceTracker = new SensorReferenceTracker( paths );
    this.adjacency = new PathAdjacency( adjacencyDefinition, paths );
    this.expiredPathsSkimmer = new ExpiredPathsSkimmer( paths );
    this.zoneUtil = new ZoneUtil( adjacency );
    setTimeSupplier( () -> now() );
  }

  @Override
  public Activation getStatus() {
    return core.getStatus();
  }

  void setTimeSupplier( Supplier<LocalDateTime> timeSupplier ) {
    this.paths.forEach( path -> path.setTimeSupplier( timeSupplier ) );
    this.expiredInPathReleaseSkimmer.setTimeSupplier( timeSupplier );
  }

  @Subscribe
  public void engagedZonesChanged( MotionSensorEvent event ) {
    core.updateStatus( () -> update( event ), ZONE_ACTIVATION_STATUS_CHANGED_INFO, status -> createStatusInfo() );
  }

  @Schedule( period = RELEASE_TIMEOUT_INTERVAL )
  void releaseTimeouts() {
    core.updateStatus( () -> release(), ZONE_ACTIVATION_STATUS_CHANGED_INFO, status -> createStatusInfo() );
  }

  private Activation release() {
    expiredPathsSkimmer.execute();
    expiredInPathReleaseSkimmer.execute( zone -> reinsert( zone ) );
    return collectStatus();
  }

  private void reinsert( Zone zone ) {
    ensureDiscretePath( zone );
    handleAdditions( zone );
  }

  private Activation update( SensorEvent<OnOff> event ) {
    doEngagedZonesChanged( event );
    return collectStatus();
  }

  private void doEngagedZonesChanged( SensorEvent<OnOff> event ) {
    if( !sensorReferenceTracker.adjustSensorReferencesOnly( event ) ) {
      ensureDiscretePaths( event );
      handleAdditions( event );
      handleRemovals( event );
      removePathDuplicates();
    }
  }

  private void ensureDiscretePaths( SensorEvent<OnOff> event ) {
    if( ON == event.getSensorStatus() ) {
      forEachAffected( event, zoneEntity -> ensureDiscretePath( zoneUtil.newZone( zoneEntity, event.getSensor() ) ) );
    }
  }

  private void ensureDiscretePath( Zone zone ) {
    if( paths.isEmpty() || !isRelatedToActivatedZones( zone.getZoneEntity() ) ) {
      paths.add( new Path() );
    }
  }

  private boolean isRelatedToActivatedZones( Entity<?> zoneEntity ) {
    return paths.stream().anyMatch( path -> adjacency.isRelatedToActivatedZones( zoneEntity, path ) );
  }

  private void handleAdditions( SensorEvent<OnOff> event ) {
    if( ON == event.getSensorStatus() ) {
      forEachAffected( event, zoneEntity -> handleAdditions( zoneUtil.newZone( zoneEntity, event.getSensor() ) ) );
    }
  }

  private void handleAdditions( Zone zone ) {
    paths.forEach( path -> updatePathWithAdditions( zone, path ) );
  }

  private void updatePathWithAdditions( Zone zone, Path path ) {
    if( path.getAll().isEmpty() || adjacency.isRelated( zone.getZoneEntity(), path ) ) {
      path.addOrReplace( zone );
    }
  }

  private void handleRemovals( SensorEvent<OnOff> event ) {
    if( OFF == event.getSensorStatus() ) {
      forEachAffected( event, zoneEntity -> handleRemovals( zoneUtil.newZone( zoneEntity, event.getSensor() ) ) );
    }
  }

  private static void forEachAffected( SensorEvent<OnOff> event, Consumer<? super Entity<?>> action ) {
    event.getAffected().stream().forEach( action );
  }

  private void handleRemovals( Zone zone ) {
    paths.forEach( path -> removeZoneFromPath( zone, path ) );
  }

  private void removeZoneFromPath( Zone zone, Path path ) {
    if( adjacency.isRelated( zone.getZoneEntity(), path ) ) {
      doRemoveZoneFromPath( zone, path );
    }
  }

  private void doRemoveZoneFromPath( Zone zone, Path path ) {
    if( hasMultipleZoneActivations( path ) ) {
      if( adjacency.isAdjacentToMoreThanOneActivation( zone.getZoneEntity(), path ) ) {
        markForInPathRelease( zone.getZoneEntity(), path );
      } else {
        removeZoneFromPathWithMultipleZoneActivations( zone.getZoneEntity(), path );
      }
    } else if( isReleaseOfElement( zone.getZoneEntity(), path ) ) {
      zoneUtil.markAsReleased( zone, path );
    }
  }

  private void removeZoneFromPathWithMultipleZoneActivations( Entity<?> zoneEntity, Path path ) {
    Set<Zone> toRemove = path.findZoneActivation( zoneEntity );
    if( isLastActive( path, path.findInPathReleases(), toRemove ) ) {
      removeLastActive( path, toRemove );
    } else if( adjacency.isAdjacentTo( zoneEntity, path.findInPathReleases() ) ) {
      markForInPathRelease( zoneEntity, path );
    } else {
      path.remove( path.findZoneActivation( zoneEntity ) );
    }
  }

  private void removePathDuplicates() {
    Set<Path> clone = new HashSet<>( paths );
    paths.clear();
    paths.addAll( clone );
  }

  private Activation collectStatus() {
    return new Activation( paths.stream().flatMap( path -> path.getAll().stream() ).collect( toSet() ) );
  }

  private String createStatusInfo() {
    return paths.stream().map( path -> new PathLogEntry( path ).toString() ).collect( joining( " | ", "[ ", " ]" ) );
  }

  private static boolean hasMultipleZoneActivations( Path path ) {
    return path.getAll().size() > 1;
  }

  private static boolean isReleaseOfElement( Entity<?> zoneEntity, Path path ) {
    return !path.findZoneActivation( zoneEntity ).isEmpty();
  }

  private static boolean isLastActive( Path path, Set<Zone> inPathReleases, Set<Zone> toRemove ) {
    return path.getAll().size() - inPathReleases.size() == toRemove.size();
  }

  private void removeLastActive( Path path, Set<Zone> toRemove ) {
    path.remove( path.findInPathReleases() );
    path.remove( toRemove );
    toRemove.forEach( zone -> zoneUtil.markAsReleased( zone, path ) );
  }
}