package com.codeaffine.home.control.test.util.entity;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.Sensor;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;

public class MyEntity implements CompositeEntity<MyEntityDefinition>, Sensor {

  private final MyEntityDefinition definition;
  private final SensorControl sensorControl;
  private final Set<Entity<?>> entities;

  public MyEntity( MyEntityDefinition definition, SensorControlFactory factory, Set<Entity<?>> entities ) {
    this.sensorControl = factory.create( this );
    this.definition = definition;
    this.entities = entities;
    entities.add( this );
  }

  @Override
  public MyEntityDefinition getDefinition() {
    return definition;
  }

  @Override
  public <R extends Entity<C>, C extends EntityDefinition<R>> Collection<R> getChildren( Class<C> childType ) {
    return emptySet();
  }

  @Override
  public <R extends Entity<C>, C extends EntityDefinition<R>> Collection<Entity<?>> getChildren() {
    return entities.stream().filter( entity -> entity != MyEntity.this ).collect( toSet() );
  }

  @Override
  public void registerZone( Entity<?> zone ) {
    sensorControl.registerZone( zone );
  }

  @Override
  public void unregisterZone( Entity<?> zone ) {
    sensorControl.unregisterZone( zone );
  }

  public void engage() {
    sensorControl.engage();
  }

  public void release() {
    sensorControl.release();
  }

  @Override
  public String toString() {
    return "MyEntity [definition=" + definition + "]";
  }
}