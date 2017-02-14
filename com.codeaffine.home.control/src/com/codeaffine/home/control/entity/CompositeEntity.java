package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface CompositeEntity {
  <E extends Entity<D>, D extends EntityDefinition<E>> Collection<E> getChildren( Class<D> childType );
}
