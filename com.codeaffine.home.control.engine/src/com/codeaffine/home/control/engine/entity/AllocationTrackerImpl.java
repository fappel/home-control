package com.codeaffine.home.control.engine.entity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.entity.AllocationTracker;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.util.Disposable;

public class AllocationTrackerImpl implements AllocationTracker, Disposable {

  private final Map<Entity<EntityDefinition<?>>, Set<Entity<EntityDefinition<?>>>> allocated;
  private final EventBus eventBus;

  public AllocationTrackerImpl( EventBus eventBus ) {
    this.eventBus = eventBus;
    this.allocated = new HashMap<>();
  }

  @Override
  public void dispose() {
    allocated.clear();
  }

  public void allocate( Entity<EntityDefinition<?>> sensor, Set<Entity<EntityDefinition<?>>> allocables ) {
    Set<Entity<EntityDefinition<?>>> additions = new HashSet<>();
    allocables.forEach( allocable -> updateOnEngage( sensor, allocable, additions ) );
    if( !additions.isEmpty() ) {
      eventBus.post( new AllocationEvent( sensor, getAllocatedInternal(), additions, emptySet() ) );
    }
  }

  public void release( Entity<EntityDefinition<?>> sensor, Set<Entity<EntityDefinition<?>>> allocables ) {
    Set<Entity<EntityDefinition<?>>> removals = new HashSet<>();
    allocables.forEach( allocable -> updateOnDeallocate( sensor, allocable, removals ) );
    if( !removals.isEmpty() ) {
      eventBus.post( new AllocationEvent( sensor, getAllocatedInternal(), emptySet(), removals ) );
    }
  }

  @Override
  public Set<Entity<?>> getAllocated() {
    return getAllocatedInternal().stream().map( entity -> ( Entity<?> )entity ).collect( toSet() );
  }

  private Set<Entity<EntityDefinition<?>>> getAllocatedInternal() {
    return defensiveCopy( allocated.keySet() );
  }

  private static Set<Entity<EntityDefinition<?>>> defensiveCopy( Set<Entity<EntityDefinition<?>>> base ) {
    return new HashSet<>( base );
  }

  private void updateOnEngage( Entity<EntityDefinition<?>> sensor,
                               Entity<EntityDefinition<?>> allocable,
                               Set<Entity<EntityDefinition<?>>> additions )
  {
    if( !allocated.containsKey( allocable ) ) {
      allocated.put( allocable, new HashSet<>( asList( sensor ) ) );
      additions.add( allocable );
    } else {
      allocated.get( allocable ).add( sensor );
    }
  }

  private void updateOnDeallocate( Entity<EntityDefinition<?>> sensor,
                                   Entity<EntityDefinition<?>> allocable,
                                   Set<Entity<EntityDefinition<?>>> removals )
  {
    if( allocated.containsKey( allocable ) ) {
      Set<Entity<EntityDefinition<?>>> sensors = allocated.get( allocable );
      sensors.remove( sensor );
      if( sensors.isEmpty() ) {
        allocated.remove( allocable );
        removals.add( allocable );
      }
    }
  }
}