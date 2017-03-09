package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class SensorEvent<S> {

  private final Set<Entity<EntityDefinition<?>>> affected;
  private final Entity<EntityDefinition<?>> sensor;
  private final S sensorStatus;

  @SafeVarargs
  public SensorEvent( Entity<EntityDefinition<?>> sensor, S sensorStatus, Entity<EntityDefinition<?>> ... affected ) {
    verifyNotNull( sensorStatus, "sensorStatus" );
    verifyNotNull( affected, "affected" );
    verifyNotNull( sensor, "sensor" );

    this.sensorStatus = sensorStatus;
    this.affected = new HashSet<>( asList( affected ) );
    this.sensor = sensor;
  }

  public Set<Entity<EntityDefinition<?>>> getAffected() {
    return new HashSet<>( affected );
  }

  public Entity<EntityDefinition<?>> getSensor() {
    return sensor;
  }

  public S getSensorStatus() {
    return sensorStatus;
  }
}