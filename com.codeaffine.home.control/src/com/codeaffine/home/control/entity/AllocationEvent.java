package com.codeaffine.home.control.entity;

import java.util.Collection;
import java.util.Optional;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public interface AllocationEvent {

  Collection<Entity<?>> getActual();

  Optional<Entity<?>> getRemoved();

  Optional<Entity<?>> getAdded();
}