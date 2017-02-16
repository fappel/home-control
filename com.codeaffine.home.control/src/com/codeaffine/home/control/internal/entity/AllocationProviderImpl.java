package com.codeaffine.home.control.internal.entity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.entity.AllocationProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.util.Disposable;

public class AllocationProviderImpl implements AllocationProvider, Disposable {

  private final Map<Entity<?>,Set<Entity<?>>> allocations;
  private final EventBus eventBus;

  public AllocationProviderImpl( EventBus eventBus ) {
    this.eventBus = eventBus;
    this.allocations = new HashMap<>();
  }

  @Override
  public void dispose() {
    allocations.clear();
  }

  public void allocate( Entity<?> actor, Collection<Entity<?>> allocatables ) {
    Set<Entity<?>> newAllocations = new HashSet<>();
    allocatables.forEach( allocatable -> updateOnAllocate( actor, allocatable, newAllocations ) );
    if( !newAllocations.isEmpty() ) {
      eventBus.post( new AllocationEventImpl( actor, getAllocations(), newAllocations, emptySet() ) );
    }
  }

  public void deallocate( Entity<?> actor, Collection<Entity<?>> allocatables ) {
    Set<Entity<?>> endedAllocations = new HashSet<>();
    allocatables.forEach( allocatable -> updateOnDeallocate( actor, allocatable, endedAllocations ) );
    if( !endedAllocations.isEmpty() ) {
      eventBus.post( new AllocationEventImpl( actor, getAllocations(), emptySet(), endedAllocations ) );
    }
  }

  @Override
  public Collection<Entity<?>> getAllocations() {
    return defensiveCopy( allocations.keySet() );
  }

  private static Collection<Entity<?>> defensiveCopy( Collection<Entity<?>> base ) {
    return new HashSet<>( base );
  }

  private void updateOnAllocate( Entity<?> actor, Entity<?> allocatable, Set<Entity<?>> newAllocations ) {
    if( !allocations.containsKey( allocatable ) ) {
      allocations.put( allocatable, new HashSet<>( asList( actor ) ) );
      newAllocations.add( allocatable );
    } else {
      allocations.get( allocatable ).add( actor );
    }
  }

  private void updateOnDeallocate( Entity<?> actor, Entity<?> allocatable, Set<Entity<?>> deallocations ) {
    if( allocations.containsKey( allocatable ) ) {
      Set<Entity<?>> actors = allocations.get( allocatable );
      actors.remove( actor );
      if( actors.isEmpty() ) {
        allocations.remove( allocatable );
        deallocations.add( allocatable );
      }
    }
  }
}