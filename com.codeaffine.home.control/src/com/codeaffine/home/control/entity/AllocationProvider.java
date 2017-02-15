package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public interface AllocationProvider {

  void allocate( Entity<?> entity );

  void deallocate( Entity<?> entity );

  Collection<Entity<?>> getAllocations();
}