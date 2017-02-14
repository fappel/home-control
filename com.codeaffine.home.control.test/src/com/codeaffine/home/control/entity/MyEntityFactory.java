package com.codeaffine.home.control.entity;

import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;

public class MyEntityFactory implements EntityFactory<MyEntity, MyEntityDefinition> {

  @Override
  public MyEntity create( MyEntityDefinition definition ) {
    return new MyEntity( definition );
  }

}