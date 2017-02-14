package com.codeaffine.home.control.entity;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class MyEntity implements Entity<MyEntityDefinition> {

  private final MyEntityDefinition definition;

  public MyEntity( MyEntityDefinition definition ) {
    this.definition = definition;
  }

  @Override
  public MyEntityDefinition getDefinition() {
    return definition;
  }
}