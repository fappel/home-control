package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public interface AllocationProvider {

  interface AllocationActor {
    void registerAllocatable( Entity<?> allocatable );
    void unregisterAllocatable( Entity<?> allocatable );
  }

  interface AllocationControlFactory {
    AllocationControl create( Entity<?> actor );
  }

  interface AllocationControl extends AllocationActor {
    void allocate();
    void deallocate();
  }

  Collection<Entity<?>> getAllocations();
}