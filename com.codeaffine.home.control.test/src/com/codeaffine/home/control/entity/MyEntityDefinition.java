package com.codeaffine.home.control.entity;

import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class MyEntityDefinition implements EntityDefinition<MyEntity> {

  private final String name;

  public MyEntityDefinition( String name ) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "MyEntityDefinition [name=" + name + "]";
  }
}