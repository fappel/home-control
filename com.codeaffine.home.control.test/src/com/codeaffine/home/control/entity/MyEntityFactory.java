package com.codeaffine.home.control.entity;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;
import com.codeaffine.home.control.internal.entity.SensorControlFactoryImpl;
import com.codeaffine.home.control.internal.entity.ZoneProviderImpl;

public class MyEntityFactory implements EntityFactory<MyEntity, MyEntityDefinition> {

  private final SensorControlFactory sensorControlFactory;
  private final Set<Entity<?>> entities;

  public MyEntityFactory( ZoneProviderImpl zoneProvider ) {
    entities = new HashSet<>();
    sensorControlFactory = new SensorControlFactoryImpl( zoneProvider );
  }

  @Override
  public MyEntity create( MyEntityDefinition definition ) {
    return new MyEntity( definition, sensorControlFactory, entities );
  }
}