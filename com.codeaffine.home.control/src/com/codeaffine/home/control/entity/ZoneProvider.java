package com.codeaffine.home.control.entity;

import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface ZoneProvider {

  public interface Sensor {
    void registerZone( Entity<?> zone );
    void unregisterZone( Entity<?> zone );
  }

  public interface SensorControlFactory {
    <E extends Entity<D>, D extends EntityDefinition<E>> SensorControl create( E sensor );
  }

  public interface SensorControl extends Sensor {
    void engage();
    void release();
  }

  Set<Entity<?>> getEngagedZones();
}