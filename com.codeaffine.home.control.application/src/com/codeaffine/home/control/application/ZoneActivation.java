package com.codeaffine.home.control.application;

import java.util.Set;

import com.codeaffine.home.control.application.control.StatusProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface ZoneActivation extends StatusProvider<Set<Entity<EntityDefinition<?>>>> {

  @Override
  Set<Entity<EntityDefinition<?>>> getStatus();
}