package com.codeaffine.home.control.test.util.entity;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorEvent;

public class MyEntityEvent extends SensorEvent<Object> {

  public MyEntityEvent( Sensor sensor, Object sensorStatus, Entity<?>[] affected ) {
    super( sensor, sensorStatus, affected );
  }
}
