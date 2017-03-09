package com.codeaffine.home.control.test.util.entity;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.AllocationTracker.Sensor;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControl;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControlFactory;

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
  public void registerAllocable( Entity<?> allocable ) {
    sensorControl.registerAllocable( allocable );
  }

  @Override
  public void unregisterAllocable( Entity<?> allocable ) {
    sensorControl.unregisterAllocable( allocable );
  }

  public void allocate() {
    sensorControl.allocate();
  }

  public void release() {
    sensorControl.release();
  }

  @Override
  public String toString() {
    return "MyEntity [definition=" + definition + "]";
  }
}