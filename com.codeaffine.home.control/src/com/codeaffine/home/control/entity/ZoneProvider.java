package com.codeaffine.home.control.entity;

import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface ZoneProvider {

  interface Sensor {
    void registerZone( Entity<?> zone );
    void unregisterZone( Entity<?> zone );
  }

  interface SensorControlFactory {
    <E extends Entity<D>, D extends EntityDefinition<E>> SensorControl create( E sensor );
  }

  interface SensorControl extends Sensor {
    void engage();
    void release();
  }

  Set<Entity<EntityDefinition<?>>> getEngagedZones();
}