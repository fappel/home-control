package com.codeaffine.home.control.application.sensor;

import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class ActivationEvent extends SensorEvent<OnOff> {

  @SafeVarargs
  public ActivationEvent( Sensor sensor, OnOff sensorStatus, Entity<?> ... affected ) {
    super( sensor, sensorStatus, affected );
  }
}