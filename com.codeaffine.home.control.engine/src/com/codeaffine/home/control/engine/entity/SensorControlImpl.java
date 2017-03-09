package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControl;

class SensorControlImpl implements SensorControl {

  private final Set<Entity<EntityDefinition<?>>> allocated;
  private final AllocationTrackerImpl allocationTracker;
  private final Entity<EntityDefinition<?>> sensor;

  private boolean active;

  public SensorControlImpl( Entity<?> sensor, AllocationTrackerImpl allocationTracker ) {
    this.allocationTracker = allocationTracker;
    this.sensor = cast( sensor );
    this.allocated = new HashSet<>();
    this.active = false;
  }

  @Override
  public void registerAllocable( Entity<?> allocable ) {
    verifyNotNull( allocable, "allocable" );

    if( active && !allocated.contains( allocable ) ) {
      allocationTracker.allocate( sensor, asSet( cast( allocable ) ) );
    }
    allocated.add( cast( allocable ) );
  }

  @Override
  public void unregisterAllocable( Entity<?> allocable ) {
    verifyNotNull( allocable, "allocable" );

    if( active && allocated.contains( allocable ) ) {
      allocationTracker.release( sensor, asSet( cast( allocable ) ) );
    }
    allocated.remove( allocable );
  }

  @Override
  public void allocate() {
    allocationTracker.allocate( sensor, copyAllocated() );
    active = true;
  }

  @Override
  public void release() {
    allocationTracker.release( sensor, copyAllocated() );
    active = false;
  }

  private Set<Entity<EntityDefinition<?>>> copyAllocated() {
    return new HashSet<>( allocated );
  }

  @SuppressWarnings("unchecked")
  private static Entity<EntityDefinition<?>> cast( Entity<?> entity ) {
    return ( Entity<EntityDefinition<?>> )entity;
  }
}