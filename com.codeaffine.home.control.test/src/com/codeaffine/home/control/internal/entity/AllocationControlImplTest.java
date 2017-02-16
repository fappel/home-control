package com.codeaffine.home.control.internal.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.AllocationProvider.AllocationControl;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.internal.event.EventBusImpl;

public class AllocationControlImplTest {

  private AllocationProviderImpl allocationProvider;
  private AllocationControl control;
  private Entity<?> allocatable;

  @Before
  public void setUp() {
    allocationProvider = new AllocationProviderImpl( new EventBusImpl() );
    AllocationControlFactoryImpl factory = new AllocationControlFactoryImpl( allocationProvider );
    Entity<?> actor = mock( Entity.class );
    control = factory.create( actor );
    allocatable = mock( Entity.class );
  }

  @Test
  public void allocate() {
    control.registerAllocatable( allocatable );

    control.allocate();

    assertThat( allocationProvider.getAllocations() ).contains( allocatable );
  }

  @Test
  public void allocateWithoutRegisteredAllocatable() {
    control.allocate();

    assertThat( allocationProvider.getAllocations() ).isEmpty();
  }

  @Test
  public void registerAllocatableAfterAllocated() {
    control.allocate();

    control.registerAllocatable( allocatable );

    assertThat( allocationProvider.getAllocations() ).contains( allocatable );
  }

  @Test
  public void registerAllocatableAfterAllocateIfAlreadyAllocated() {
    control.registerAllocatable( allocatable );
    control.allocate();

    control.registerAllocatable( allocatable );

    assertThat( allocationProvider.getAllocations() ).contains( allocatable );
  }

  @Test
  public void deallocate() {
    control.registerAllocatable( allocatable );
    control.allocate();

    control.deallocate();

    assertThat( allocationProvider.getAllocations() ).isEmpty();
  }

  @Test
  public void unregisterAllocatableAfterAllocate() {
    control.registerAllocatable( allocatable );

    control.allocate();
    control.unregisterAllocatable( allocatable );

    assertThat( allocationProvider.getAllocations() ).isEmpty();
  }
  @Test
  public void unregisterAllocatableAfterDeallocate() {
    control.registerAllocatable( allocatable );
    control.allocate();

    control.deallocate();
    control.unregisterAllocatable( allocatable );

    assertThat( allocationProvider.getAllocations() ).isEmpty();
  }

  @Test
  public void unregisterAllocatableTwiceAfterAllocate() {
    control.registerAllocatable( allocatable );
    control.allocate();

    control.unregisterAllocatable( allocatable );
    control.unregisterAllocatable( allocatable );

    assertThat( allocationProvider.getAllocations() ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerAllocatableWithNullAsAllocatable() {
    control.registerAllocatable( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterAllocatableWithNullAsAllocatable() {
    control.unregisterAllocatable( null );
  }
}
