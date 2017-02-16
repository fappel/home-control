package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public interface AllocationEvent {
  Collection<Entity<?>> getAllocations();
  Collection<Entity<?>> getRemovals();
  Collection<Entity<?>> getAdditions();
  Entity<?> getActor();
}