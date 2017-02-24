package com.codeaffine.home.control.application;

import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface ZoneActivation {

  Set<Entity<EntityDefinition<?>>> getActiveZones();
}