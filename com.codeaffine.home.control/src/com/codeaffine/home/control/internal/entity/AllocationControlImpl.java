package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.entity.AllocationProvider.AllocationControl;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

class AllocationControlImpl implements AllocationControl {

  private final AllocationProviderImpl allocationProvider;
  private final Collection<Entity<?>> allocatables;
  private final Entity<?> actor;

  private boolean active;

  public AllocationControlImpl( Entity<?> actor, AllocationProviderImpl allocationProvider ) {
    this.allocationProvider = allocationProvider;
    this.actor = actor;
    this.allocatables = new HashSet<>();
    this.active = false;
  }

  @Override
  public void registerAllocatable( Entity<?> allocatable ) {
    verifyNotNull( allocatable, "allocatable" );

    if( active && !allocatables.contains( allocatable ) ) {
      allocationProvider.allocate( actor, asList( allocatable ) );
    }
    allocatables.add( allocatable );
  }

  @Override
  public void unregisterAllocatable( Entity<?> allocatable ) {
    verifyNotNull( allocatable, "allocatable" );

    if( active && allocatables.contains( allocatable ) ) {
      allocationProvider.deallocate( actor, asList( allocatable ) );
    }
    allocatables.remove( allocatable );
  }

  @Override
  public void allocate() {
    allocationProvider.allocate( actor, copyAllocatables() );
    active = true;
  }

  @Override
  public void deallocate() {
    allocationProvider.deallocate( actor, copyAllocatables() );
    active = false;
  }

  private Set<Entity<?>> copyAllocatables() {
    return new HashSet<>( allocatables );
  }
}