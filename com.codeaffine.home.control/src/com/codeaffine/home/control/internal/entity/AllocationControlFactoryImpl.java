package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.entity.AllocationProvider.AllocationControl;
import com.codeaffine.home.control.entity.AllocationProvider.AllocationControlFactory;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class AllocationControlFactoryImpl implements AllocationControlFactory {

  private final AllocationProviderImpl allocationProvider;

  public AllocationControlFactoryImpl( AllocationProviderImpl allocationProvider ) {
    this.allocationProvider = allocationProvider;
  }

  @Override
  public AllocationControl create( Entity<?> actor ) {
    verifyNotNull( actor, "actor" );

    return new AllocationControlImpl( actor, allocationProvider );
  }
}