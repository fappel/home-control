package com.codeaffine.home.control.entity;

import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface AllocationTracker {

  public interface Sensor {
    void registerAllocable( Entity<?> allocable );
    void unregisterAllocable( Entity<?> allocable );
  }

  public interface SensorControlFactory {
    <E extends Entity<D>, D extends EntityDefinition<E>> SensorControl create( E sensor );
  }

  public interface SensorControl extends Sensor {
    void allocate();
    void release();
  }

  Set<Entity<?>> getAllocated();
}