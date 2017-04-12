package com.codeaffine.home.control.status.model;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorEvent;

public class LightEvent extends SensorEvent<Integer>{

  public LightEvent( Sensor sensor, Integer sensorStatus, Entity<?>[] affected ) {
    super( sensor, sensorStatus, affected );
  }
}