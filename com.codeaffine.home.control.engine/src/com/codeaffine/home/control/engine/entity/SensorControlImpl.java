package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;

class SensorControlImpl implements SensorControl {

  private final Set<Entity<EntityDefinition<?>>> affected;
  private final Entity<EntityDefinition<?>> sensor;
  private final EventBus eventBus;

  public SensorControlImpl( Entity<?> sensor, EventBus eventBus ) {
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( sensor, "sensor" );

    this.affected = new HashSet<>();
    this.sensor = cast( sensor );
    this.eventBus = eventBus;
  }

  @Override
  public void registerAffected( Entity<?> affected ) {
    verifyNotNull( affected, "affected" );

    this.affected.add( cast( affected ) );
  }

  @Override
  public void unregisterAffected( Entity<?> affected ) {
    verifyNotNull( affected, "affected" );

    this.affected.remove( affected );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S> void notifyAboutSensorStatusChange( S sensorStatus ) {
    verifyNotNull( sensorStatus, "sensorStatus" );

    eventBus.post( new SensorEvent<S>( sensor, sensorStatus, affected.toArray( new Entity[ affected.size() ] ) ) );
  }

  @SuppressWarnings("unchecked")
  private static Entity<EntityDefinition<?>> cast( Entity<?> entity ) {
    return ( Entity<EntityDefinition<?>> )entity;
  }
}