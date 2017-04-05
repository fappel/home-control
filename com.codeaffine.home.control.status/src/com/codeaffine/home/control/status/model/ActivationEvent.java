package com.codeaffine.home.control.status.model;

import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.status.type.OnOff;

public class ActivationEvent extends SensorEvent<OnOff> {

  @SafeVarargs
  public ActivationEvent( Sensor sensor, OnOff sensorStatus, Entity<?> ... affected ) {
    super( sensor, sensorStatus, affected );
  }
}