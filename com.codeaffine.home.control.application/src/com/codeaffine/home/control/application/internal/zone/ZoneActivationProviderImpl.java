package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.Messages.ZONE_ACTIVATION_STATUS_CHANGED_INFO;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorEvent;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusProviderCore;

public class ZoneActivationProviderImpl implements ZoneActivationProvider {

  static final long IN_PATH_RELEASES_EXPIRATION_TIME = 10L;
  static final long RELEASE_TIMEOUT_INTERVAL = 20L;
  static final long PATH_EXPIRED_TIMEOUT = 60L;

  private final ExpiredInPathReleaseSkimmer expiredInPathReleaseSkimmer;
  private final StatusProviderCore<Set<ZoneActivation>> core;
  private final ExpiredPathsSkimmer expiredPathsSkimmer;
  private final PathAdjacency adjacency;
  private final Set<Path> paths;

  public ZoneActivationProviderImpl( AdjacencyDefinition adjacencyDefinition, EventBus eventBus, Logger logger ) {
    verifyNotNull( adjacencyDefinition, "adjacencyDefinition" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.core = new StatusProviderCore<>( eventBus, emptySet(), this, logger );
    this.paths = new HashSet<>();
    this.expiredInPathReleaseSkimmer = new ExpiredInPathReleaseSkimmer( paths );
    this.adjacency = new PathAdjacency( adjacencyDefinition, paths );
    this.expiredPathsSkimmer = new ExpiredPathsSkimmer( paths );
    setTimeSupplier( () -> now() );
  }

  @Override
  public Set<ZoneActivation> getStatus() {
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

  private Set<ZoneActivation> release() {
    expiredPathsSkimmer.execute();
    expiredInPathReleaseSkimmer.execute( zone -> reinsert( zone ) );
    return collectStatus();
  }

  private void reinsert( Entity<?> zone ) {
    ensureDiscretePath( zone );
    handleAdditions( zone );
  }

  private Set<ZoneActivation> update( SensorEvent<OnOff> event ) {
    doEngagedZonesChanged( event );
    return collectStatus();
  }

  private void doEngagedZonesChanged( SensorEvent<OnOff> event ) {
    ensureDiscretePaths( event );
    handleAdditions( event );
    handleRemovals( event );
    removePathDuplicates();
  }

  private void ensureDiscretePaths( SensorEvent<OnOff> event ) {
    if( ON == event.getSensorStatus() ) {
      event.getAffected().stream().forEach( zone -> ensureDiscretePath( zone ) );
    }
  }

  private void ensureDiscretePath( Entity<?> zone ) {
    if( paths.isEmpty() || !belongsToPathWithActivatedZones( zone ) ) {
      paths.add( new Path() );
    }
  }

  private boolean belongsToPathWithActivatedZones( Entity<?> zone ) {
    return paths.stream().anyMatch( path -> adjacency.belongsToPathWithActivatedZones( zone, path ) );
  }

  private void handleAdditions( SensorEvent<OnOff> event ) {
    if( ON == event.getSensorStatus() ) {
      event.getAffected().stream().forEach( zone -> handleAdditions( zone ) );
    }
  }

  private void handleAdditions( Entity<?> zone ) {
    paths.forEach( path -> updatePathWithAdditions( zone, path ) );
  }

  private void updatePathWithAdditions( Entity<?> zone, Path path ) {
    if( path.isEmpty() || adjacency.belongsToPath( zone, path ) ) {
      path.addOrReplace( new ZoneActivationImpl( zone, adjacency ) );
    }
  }

  private void handleRemovals( SensorEvent<OnOff> event ) {
    if( OFF == event.getSensorStatus() ) {
      event.getAffected().stream().forEach( zone -> handleRemovals( zone ) );
    }
  }

  private void handleRemovals( Entity<?> zone ) {
    paths.forEach( path -> removeZoneFromPath( zone, path ) );
  }

  private void removeZoneFromPath( Entity<?> zone, Path path ) {
    if( adjacency.belongsToPath( zone, path ) ) {
      doRemoveZoneFromPath( zone, path );
    }
  }

  private void doRemoveZoneFromPath( Entity<?> zone, Path path ) {
    if( hasMultipleZoneActivations( path ) ) {
      if( adjacency.isInPathRelease( zone, path ) ) {
        markForInPathRelease( zone, path );
      } else {
        removeZoneFromPathWithMultipleZoneActivations( zone, path );
      }
    } else {
      markAsReleased( zone, path );
    }
  }

  private void removeZoneFromPathWithMultipleZoneActivations( Entity<?> zone, Path path ) {
    Set<ZoneActivation> toRemove = path.findZoneActivation( zone );
    if( isLastActive( path, path.findInPathReleases(), toRemove ) ) {
      removeLastActive( path, toRemove );
    } else if( adjacency.isAdjacentToInPathRelease( zone, path.findInPathReleases() ) ) {
      markForInPathRelease( zone, path );
    } else {
      path.remove( path.findZoneActivation( zone ) );
    }
  }

  private void removePathDuplicates() {
    Set<Path> clone = new HashSet<>( paths );
    paths.clear();
    paths.addAll( clone );
  }

  private Set<ZoneActivation> collectStatus() {
    return paths.stream().flatMap( path -> path.getAll().stream() ).collect( toSet() );
  }

  private String createStatusInfo() {
    return paths.stream().map( path -> new PathLogEntry( path ).toString() ).collect( joining( " | ", "[ ", " ]" ) );
  }

  private static boolean hasMultipleZoneActivations( Path path ) {
    return path.size() > 1;
  }

  private static boolean isLastActive( Path path, Set<ZoneActivation> inPathReleases, Set<ZoneActivation> toRemove ) {
    return path.size() - inPathReleases.size() == toRemove.size();
  }

  private void removeLastActive( Path path, Set<ZoneActivation> toRemove ) {
    path.remove( path.findInPathReleases() );
    path.remove( toRemove );
    toRemove.forEach( activation -> markAsReleased( activation.getZone(), path ) );
  }

  private static void markForInPathRelease( Entity<?> zone, Path path ) {
    path.findZoneActivation( zone ).forEach( elem -> ( ( ZoneActivationImpl )elem ).markForInPathRelease() );
  }

  private void markAsReleased( Entity<?> zone, Path path ) {
    ZoneActivationImpl zoneActivation = new ZoneActivationImpl( zone, adjacency );
    zoneActivation.markRelease();
    path.addOrReplace( zoneActivation );
  }
}