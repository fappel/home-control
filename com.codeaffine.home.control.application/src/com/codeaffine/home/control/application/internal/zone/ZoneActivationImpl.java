package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.Messages.ZONE_ACTIVATION_STATUS_CHANGED_INFO;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.codeaffine.home.control.application.ZoneActivation;
import com.codeaffine.home.control.application.control.StatusProviderCore;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.logger.Logger;

public class ZoneActivationImpl implements ZoneActivation {

  private final StatusProviderCore<Set<Entity<EntityDefinition<?>>>> statusProviderCore;
  private final Set<List<Entity<EntityDefinition<?>>>> traces;
  private final AdjacencyDefinition adjacencyDefinitions;

  public ZoneActivationImpl( AdjacencyDefinition adjacencyDefinition, EventBus eventBus, Logger logger ) {
    verifyNotNull( adjacencyDefinition, "adjacencyDefinition" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.statusProviderCore = new StatusProviderCore<>( eventBus, emptySet(), this, logger );
    this.adjacencyDefinitions = adjacencyDefinition;
    this.traces = new HashSet<>();
  }

  @Override
  public Set<Entity<EntityDefinition<?>>> getStatus() {
    return statusProviderCore.getStatus();
  }

  @Subscribe
  public void engagedZonesChanged( ZoneEvent event ) {
    statusProviderCore.updateStatus( () -> update( event ),
                                     ZONE_ACTIVATION_STATUS_CHANGED_INFO,
                                     status -> createListOfEngagedZoneDefinitions() );
  }

  private Set<Entity<EntityDefinition<?>>> update( ZoneEvent event ) {
    doEngagedZonesChanged( event );
    return traces.stream().flatMap( stack -> stack.stream() ).collect( toSet() );
  }

  private void doEngagedZonesChanged( ZoneEvent event ) {
    ensureDiscreteTraces( event );
    handleAdditions( event );
    handleRemovals( event );
    removeTraceDuplicates();
  }

  private void ensureDiscreteTraces( ZoneEvent event ) {
    event.getAdditions().stream().forEach( zone -> ensureDescreteTrace( zone ) );
  }

  private void ensureDescreteTrace( Entity<EntityDefinition<?>> zone ) {
    if( traces.isEmpty() || !belongsToExistingTrace( zone ) ) {
      traces.add( new LinkedList<>() );
    }
  }

  private void handleAdditions( ZoneEvent event ) {
    event.getAdditions().stream().forEach( zone -> handleAdditions( zone ) );
  }

  private void handleAdditions( Entity<EntityDefinition<?>> zone ) {
    traces.forEach( trace -> updateTraceWithAdditions( zone, trace ) );
  }

  private void updateTraceWithAdditions( Entity<EntityDefinition<?>> zone, List<Entity<EntityDefinition<?>>> trace ) {
    if( trace.isEmpty() || belongsToTrace( zone, trace ) ) {
      trace.remove( zone );
      trace.add( zone );
    }
  }

  private void handleRemovals( ZoneEvent event ) {
    event.getRemovals().stream().forEach( zone -> handleRemovals( zone ) );
  }

  private void handleRemovals( Entity<EntityDefinition<?>> zone ) {
    traces.forEach( trace -> removeZoneFromTrace( zone, trace ) );
  }

  private static void removeZoneFromTrace( Entity<EntityDefinition<?>> zone, List<Entity<EntityDefinition<?>>> trace ) {
    if( trace.size() > 1 ) {
      trace.remove( zone );
    }
  }

  private void removeTraceDuplicates() {
    HashSet<List<Entity<EntityDefinition<?>>>> clone = new HashSet<>( traces );
    traces.clear();
    traces.addAll( clone );
  }

  private boolean belongsToExistingTrace( Entity<EntityDefinition<?>> zone ) {
    return traces.stream().anyMatch( trace -> belongsToTrace( zone, trace ) );
  }

  private boolean belongsToTrace( Entity<EntityDefinition<?>> zone, List<Entity<EntityDefinition<?>>> trace ) {
    return trace.stream().anyMatch( traced -> isAdjacent( traced, zone ) || zone == traced );
  }

  private boolean isAdjacent( Entity<EntityDefinition<?>> zone1, Entity<EntityDefinition<?>> zone2 ) {
    return adjacencyDefinitions.isAdjacent( zone1.getDefinition(), zone2.getDefinition() );
  }

  private String createListOfEngagedZoneDefinitions() {
    return "[ " + traces.stream().map( stack -> createListOfZonesInStack( stack ) ).collect( joining( " | " ) ) + " ]";
  }

  private static String createListOfZonesInStack( List<Entity<EntityDefinition<?>>> stack ) {
    return stack.stream().map( zone -> zone.getDefinition().toString() ).collect( joining( ", " ) );
  }
}