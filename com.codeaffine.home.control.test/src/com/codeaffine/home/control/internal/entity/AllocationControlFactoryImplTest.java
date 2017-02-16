package com.codeaffine.home.control.internal.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.AllocationProvider.AllocationControl;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.internal.event.EventBusImpl;

public class AllocationControlFactoryImplTest {

  private AllocationProviderImpl allocationProvider;
  private AllocationControlFactoryImpl factory;


  @Before
  public void setUp() {
    allocationProvider = new AllocationProviderImpl( new EventBusImpl() );
    factory = new AllocationControlFactoryImpl( allocationProvider );
  }

  @Test
  public void create() {
    Entity<?> actor = mock( Entity.class );
    Entity<?> expected = mock( Entity.class );

    AllocationControl control = factory.create( actor );
    control.registerAllocatable( expected );
    control.allocate();

    assertThat( allocationProvider.getAllocations() ).contains( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsActorArgument() {
    factory.create( null );
  }
}