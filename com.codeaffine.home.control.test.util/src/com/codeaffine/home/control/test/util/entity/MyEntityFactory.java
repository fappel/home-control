package com.codeaffine.home.control.test.util.entity;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;

public class MyEntityFactory implements EntityFactory<MyEntity, MyEntityDefinition> {

  private final SensorControlFactory sensorControlFactory;
  private final Set<Entity<?>> entities;

  public MyEntityFactory( SensorControlFactory sensorControlFactory ) {
    this.sensorControlFactory = sensorControlFactory;
    entities = new HashSet<>();
  }

  @Override
  public MyEntity create( MyEntityDefinition definition ) {
    return new MyEntity( definition, sensorControlFactory, entities );
  }
}