package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.event.EventBus;

public class SensorControlFactoryImpl implements SensorControlFactory {

  private final EventBus eventBus;

  public SensorControlFactoryImpl( EventBus eventBus ) {
    verifyNotNull( eventBus, "eventBus" );

    this.eventBus = eventBus;
  }

  @Override
  public  SensorControl create( Sensor sensor ) {
    verifyNotNull( sensor, "sensor" );

    return new SensorControlImpl( sensor, eventBus );
  }
}