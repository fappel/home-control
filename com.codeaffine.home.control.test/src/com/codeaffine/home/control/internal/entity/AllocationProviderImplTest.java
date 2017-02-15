package com.codeaffine.home.control.internal.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.internal.event.EventBusImpl;

public class AllocationProviderImplTest {

  private static final Entity<?> ENTITY_1 = mock( Entity.class );
  private static final Entity<?> ENTITY_2 = mock( Entity.class );

  private AllocationProviderImpl provider;
  private EventBusImpl eventBus;
  private EventCaptor captor;

  static class EventCaptor {

    private AllocationEvent event;

    @Subscribe void captureEvent( AllocationEvent event ) {
      this.event = event;
    }
  }

  @Before
  public void setUp() {
    captor = new EventCaptor();
    eventBus = new EventBusImpl();
    provider = new AllocationProviderImpl( eventBus );
  }

  @Test
  public void allocate() {
    provider.allocate( ENTITY_1 );

    eventBus.register( captor );
    provider.allocate( ENTITY_2 );

    assertThat( captor.event.getActual() ).contains( ENTITY_1, ENTITY_2 );
    assertThat( captor.event.getAdded() ).contains( ENTITY_2 );
    assertThat( captor.event.getRemoved() ).isEmpty();
    assertThat( provider.getAllocations() ).contains( ENTITY_1, ENTITY_2 );
  }

  @Test
  public void allocateIfEmpty() {
    eventBus.register( captor );
    provider.allocate( ENTITY_1 );

    assertThat( captor.event.getActual() ).contains( ENTITY_1 );
    assertThat( captor.event.getAdded() ).contains( ENTITY_1 );
    assertThat( captor.event.getRemoved() ).isEmpty();
    assertThat( provider.getAllocations() ).contains( ENTITY_1 );
  }

  @Test
  public void allocateIfEntityIsAlreadyAllocated() {
    provider.allocate( ENTITY_1 );

    eventBus.register( captor );
    provider.allocate( ENTITY_1 );

    assertThat( captor.event ).isNull();
    assertThat( provider.getAllocations() ).contains( ENTITY_1 );
  }

  @Test
  public void deallocate() {
    provider.allocate( ENTITY_1 );
    provider.allocate( ENTITY_2 );

    eventBus.register( captor );
    provider.deallocate( ENTITY_2 );

    assertThat( captor.event.getActual() ).contains( ENTITY_1 );
    assertThat( captor.event.getAdded() ).isEmpty();
    assertThat( captor.event.getRemoved() ).contains( ENTITY_2 );
    assertThat( provider.getAllocations() ).contains( ENTITY_1 );
  }

  @Test
  public void deallocateLastElement() {
    provider.allocate( ENTITY_1 );

    eventBus.register( captor );
    provider.deallocate( ENTITY_1 );

    assertThat( captor.event.getActual() ).isEmpty();
    assertThat( captor.event.getAdded() ).isEmpty();
    assertThat( captor.event.getRemoved() ).contains( ENTITY_1 );
    assertThat( provider.getAllocations() ).isEmpty();
  }

  @Test
  public void deallocateIfEntityIsNotAllocated() {
    provider.allocate( ENTITY_1 );

    eventBus.register( captor );
    provider.deallocate( ENTITY_2 );

    assertThat( captor.event ).isNull();
    assertThat( provider.getAllocations() ).contains( ENTITY_1 );
  }

  @Test
  public void changeAllocationsReturnValue() {
    eventBus.register( captor );
    provider.allocate( ENTITY_1 );

    provider.getAllocations().add( ENTITY_2 );
    captor.event.getActual().add( ENTITY_2 );

    assertThat( provider.getAllocations() ).hasSize( 1 );
  }

  @Test
  public void dispose() {
    provider.allocate( ENTITY_1 );

    provider.dispose();

    assertThat( provider.getAllocations() ).isEmpty();
  }
}