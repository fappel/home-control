package com.codeaffine.home.control.entity;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Stream;

public class MyEntityProvider extends BaseEntityProvider<MyEntity, MyEntityDefinition> {

  public static final List<MyEntityDefinition> MY_ENTITY_DEFINITIONS
    = asList( new MyEntityDefinition(), new MyEntityDefinition() );

  public MyEntityProvider() {
    super( new MyEntityFactory() );
  }

  @Override
  protected Stream<MyEntityDefinition> getStreamOfDefinitions() {
    return MY_ENTITY_DEFINITIONS.stream();
  }
}