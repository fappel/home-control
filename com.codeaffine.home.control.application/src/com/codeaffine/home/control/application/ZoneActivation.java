package com.codeaffine.home.control.application;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.application.internal.allocation.AdjacencyDefinition;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneEvent;
import com.codeaffine.home.control.event.Subscribe;

public class ZoneActivation {

  private final AdjacencyDefinition adjacencyDefinitions;
  private final Set<List<Entity<EntityDefinition<?>>>> traces;

  public ZoneActivation( AdjacencyDefinition adjacencyDefinition ) {
    this.adjacencyDefinitions = adjacencyDefinition;
    this.traces = new HashSet<>();
  }

  public Set<Entity<EntityDefinition<?>>> getActiveZones() {
    return traces.stream().flatMap( stack -> stack.stream() ).collect( toSet() );
  }

  @Subscribe
  public void engagedZonesChanged( ZoneEvent event ) {
    ensureDiscreteTraces( event );
    handleAdditions( event );
    handleRemovals( event );
    removeTraceDuplicates();
    LoggerFactory.getLogger( ZoneActivation.class ).info( "Active Zones" + getActiveZones() );
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
}