package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.BiFunction;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;

public class SensorControlFactoryImpl implements SensorControlFactory {

  private final EventBus eventBus;

  public SensorControlFactoryImpl( EventBus eventBus ) {
    verifyNotNull( eventBus, "eventBus" );

    this.eventBus = eventBus;
  }

  @Override
  public SensorControl create( Sensor sensor, BiFunction<Object, Entity<?>[], SensorEvent<?>> eventFactory ) {
    verifyNotNull( eventFactory, "eventFactory" );
    verifyNotNull( sensor, "sensor" );

    return new SensorControlImpl( sensor, eventFactory, eventBus );
  }
}