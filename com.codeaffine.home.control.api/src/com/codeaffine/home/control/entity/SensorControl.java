package com.codeaffine.home.control.entity;

import java.util.function.BiFunction;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public interface SensorControl extends Sensor {

  public interface SensorControlFactory {
    SensorControl create( Sensor sensor, BiFunction<Object, Entity<?>[], SensorEvent<?>> eventFactory );
  }

  <S> void notifyAboutSensorStatusChange( S sensorStatus );
}