package com.codeaffine.home.control.entity;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface SensorControl extends Sensor {

  public interface SensorControlFactory {
    <E extends Entity<D>, D extends EntityDefinition<E>> SensorControl create( E sensor );
  }

  <S> void notifyAboutSensorStatusChange( S sensorStatus );
}