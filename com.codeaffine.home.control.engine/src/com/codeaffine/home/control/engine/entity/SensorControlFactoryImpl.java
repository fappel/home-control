package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
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
  public <E extends Entity<D>, D extends EntityDefinition<E>> SensorControl create( E sensor ) {
    verifyNotNull( sensor, "sensor" );

    return new SensorControlImpl( sensor, eventBus );
  }
}