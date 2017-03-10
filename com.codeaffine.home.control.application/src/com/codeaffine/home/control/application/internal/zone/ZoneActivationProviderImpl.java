package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.Messages.*;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorEvent;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusProviderCore;

public class ZoneActivationProviderImpl implements ZoneActivationProvider {

  private final StatusProviderCore<Set<ZoneActivation>> statusProviderCore;
  private final AdjacencyDefinition adjacencyDefinitions;
  private final Set<List<ZoneActivation>> traces;

  public ZoneActivationProviderImpl( AdjacencyDefinition adjacencyDefinition, EventBus eventBus, Logger logger ) {
    verifyNotNull( adjacencyDefinition, "adjacencyDefinition" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.statusProviderCore = new StatusProviderCore<>( eventBus, emptySet(), this, logger );
    this.adjacencyDefinitions = adjacencyDefinition;
    this.traces = new HashSet<>();
  }

  @Override
  public Set<ZoneActivation> getStatus() {
    return statusProviderCore.getStatus();
  }

  @Subscribe
  public void engagedZonesChanged( MotionSensorEvent event ) {
    statusProviderCore.updateStatus( () -> update( event ),
                                     ZONE_ACTIVATION_STATUS_CHANGED_INFO,
                                     status -> createListOfEngagedZoneDefinitions() );
  }

  private Set<ZoneActivation> update( SensorEvent<OnOff> event ) {
    doEngagedZonesChanged( event );
    return traces.stream().flatMap( stack -> stack.stream() ).collect( toSet() );
  }

  private void doEngagedZonesChanged( SensorEvent<OnOff> event ) {
    ensureDiscreteTraces( event );
    handleAdditions( event );
    handleRemovals( event );
    removeTraceDuplicates();
  }

  private void ensureDiscreteTraces( SensorEvent<OnOff> event ) {
    if( ON == event.getSensorStatus() ) {
      event.getAffected().stream().forEach( zone -> ensureDiscreteTrace( zone ) );
    }
  }

  private void ensureDiscreteTrace( Entity<?> zone ) {
    if( traces.isEmpty() || !belongsToExistingTrace( zone ) ) {
      traces.add( new LinkedList<>() );
    }
  }

  private void handleAdditions( SensorEvent<OnOff> event ) {
    if( ON == event.getSensorStatus() ) {
      event.getAffected().stream().forEach( zone -> handleAdditions( zone ) );
    }
  }

  private void handleAdditions( Entity<?> zone ) {
    traces.forEach( trace -> updateTraceWithAdditions( zone, trace ) );
  }

  private void updateTraceWithAdditions( Entity<?> zone, List<ZoneActivation> trace ) {
    if( trace.isEmpty() || belongsToTrace( zone, trace ) ) {
      trace.removeAll( collectZonesToRemove( zone, trace ) );
      trace.add( new ZoneActivationImpl( zone ) );
    }
  }

  private void handleRemovals( SensorEvent<OnOff> event ) {
    if( OFF == event.getSensorStatus() ) {
      event.getAffected().stream().forEach( zone -> handleRemovals( zone ) );
    }
  }

  private void handleRemovals( Entity<?> zone ) {
    traces.forEach( trace -> removeZoneFromTrace( zone, trace ) );
  }

  private static void removeZoneFromTrace( Entity<?> zone, List<ZoneActivation> trace ) {
    if( trace.size() > 1 ) {
      trace.removeAll( collectZonesToRemove( zone, trace ) );
    } else if( trace.size() == 1 ) {
      ZoneActivationImpl zoneActivation = new ZoneActivationImpl( trace.remove( 0 ).getZone() );
      zoneActivation.markRelease();
      trace.add( zoneActivation );
    }
  }

  private static Set<ZoneActivation> collectZonesToRemove( Entity<?> zone, List<ZoneActivation> trace ) {
    return trace.stream().filter( activation -> activation.getZone().equals( zone ) ).collect( toSet() );
  }

  private void removeTraceDuplicates() {
    Set<List<ZoneActivation>> clone = new HashSet<>( traces );
    traces.clear();
    traces.addAll( clone );
  }

  private boolean belongsToExistingTrace( Entity<?> zone ) {
    return traces.stream().anyMatch( trace -> belongsToTrace( zone, trace ) );
  }

  private boolean belongsToTrace( Entity<?> zone, List<ZoneActivation> trace ) {
    return trace.stream().anyMatch( traced -> isAdjacent( traced.getZone(), zone ) || zone == traced.getZone() );
  }

  private boolean isAdjacent( Entity<?> zone1, Entity<?> zone2 ) {
    EntityDefinition<?> zoneDefinition1 = ( EntityDefinition<?> )zone1.getDefinition();
    EntityDefinition<?> zoneDefinition2 = ( EntityDefinition<?> )zone2.getDefinition();
    return adjacencyDefinitions.isAdjacent( zoneDefinition1, zoneDefinition2 );
  }

  private String createListOfEngagedZoneDefinitions() {
    return "[ " + traces.stream().map( stack -> createListOfZonesInStack( stack ) ).collect( joining( " | " ) ) + " ]";
  }

  private static String createListOfZonesInStack( List<ZoneActivation> stack ) {
    return stack.stream().map( activation -> getActivationAsString( activation ) ).collect( joining( ", " ) );
  }

  private static String getActivationAsString( ZoneActivation activation ) {
    StringBuilder result = new StringBuilder( activation.getZone().getDefinition().toString() );
    activation.getReleaseTime().ifPresent( time -> appendReleaseTime( result, time ) );
    return result.toString();
  }

  private static StringBuilder appendReleaseTime( StringBuilder result, LocalDateTime time ) {
    return result.append( " <" ).append( RELEASED_TAG ).append( " " ).append( time.toString() ).append( ">" );
  }
}