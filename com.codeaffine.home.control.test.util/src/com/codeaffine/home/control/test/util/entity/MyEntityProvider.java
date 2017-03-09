package com.codeaffine.home.control.test.util.entity;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Stream;

import com.codeaffine.home.control.entity.AllocationTracker.SensorControlFactory;
import com.codeaffine.home.control.entity.BaseEntityProvider;

public class MyEntityProvider extends BaseEntityProvider<MyEntity, MyEntityDefinition> {

  public static final MyEntityDefinition PARENT = new MyEntityDefinition( "PARENT" );
  public static final MyEntityDefinition CHILD = new MyEntityDefinition( "CHILD" );
  public static final List<MyEntityDefinition> MY_ENTITY_DEFINITIONS = asList( PARENT, CHILD );

  public MyEntityProvider( SensorControlFactory sensorControlFactory ) {
    super( new MyEntityFactory( sensorControlFactory ) );
  }

  @Override
  protected Stream<MyEntityDefinition> getStreamOfDefinitions() {
    return MY_ENTITY_DEFINITIONS.stream();
  }
}