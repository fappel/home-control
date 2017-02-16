package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.home.control.internal.entity.AllocationEventAssert.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.internal.event.EventBusImpl;

public class AllocationProviderImplTest {

  private static final Entity<?> ALLOCATABLE_1 = mock( Entity.class );
  private static final Entity<?> ALLOCATABLE_2 = mock( Entity.class );
  private static final Entity<?> ALLOCATABLE_3 = mock( Entity.class );
  private static final Entity<?> ACTOR_1 = mock( Entity.class );
  private static final Entity<?> ACTOR_2 = mock( Entity.class );

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
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    eventBus.register( captor );
    provider.allocate( ACTOR_2, asList( ALLOCATABLE_2, ALLOCATABLE_3 ) );

    assertThat( captor.event )
      .hasActor( ACTOR_2 )
      .hasAllocations(  ALLOCATABLE_1, ALLOCATABLE_2, ALLOCATABLE_3 )
      .hasAdditions( ALLOCATABLE_2, ALLOCATABLE_3 )
      .hasNoRemovals();
    assertThat( provider.getAllocations() )
      .contains( ALLOCATABLE_1, ALLOCATABLE_2 );
  }

  @Test
  public void allocateIfEmpty() {
    eventBus.register( captor );
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    assertThat( captor.event )
      .hasActor( ACTOR_1 )
      .hasAllocations( ALLOCATABLE_1 )
      .hasAdditions( ALLOCATABLE_1 )
      .hasNoRemovals();
    assertThat( provider.getAllocations() )
      .contains( ALLOCATABLE_1 );
  }

  @Test
  public void allocateIfEntityIsAlreadyAllocated() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    eventBus.register( captor );
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getAllocations() ).contains( ALLOCATABLE_1 );
  }

  @Test
  public void allocateSameAllocatableByDifferentActors() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    eventBus.register( captor );
    provider.allocate( ACTOR_2, asList( ALLOCATABLE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getAllocations() ).contains( ALLOCATABLE_1 );
  }

  @Test
  public void allocateIfWithoutAllocatable() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    eventBus.register( captor );
    provider.allocate( ACTOR_2, emptySet() );

    assertThat( captor.event ).isNull();
    assertThat( provider.getAllocations() ).contains( ALLOCATABLE_1 );
  }

  @Test
  public void deallocate() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );
    provider.allocate( ACTOR_2, asList( ALLOCATABLE_2, ALLOCATABLE_3 ) );

    eventBus.register( captor );
    provider.deallocate( ACTOR_2, asList( ALLOCATABLE_2, ALLOCATABLE_3 ) );

    assertThat( captor.event )
      .hasActor( ACTOR_2 )
      .hasAllocations( ALLOCATABLE_1 )
      .hasNoAdditions()
      .hasRemovals( ALLOCATABLE_2, ALLOCATABLE_3 );
    assertThat( provider.getAllocations() )
      .contains( ALLOCATABLE_1 );
  }

  @Test
  public void deallocateLastElement() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    eventBus.register( captor );
    provider.deallocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    assertThat( captor.event )
      .hasActor( ACTOR_1 )
      .hasNoAllocations()
      .hasNoAdditions()
      .hasRemovals( ALLOCATABLE_1 );
    assertThat( provider.getAllocations() )
      .isEmpty();
  }

  @Test
  public void deallocateIfEntityIsNotAllocated() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    eventBus.register( captor );
    provider.deallocate( ACTOR_2, asList( ALLOCATABLE_2 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getAllocations() ).contains( ALLOCATABLE_1 );
  }

  @Test
  public void deallocateByOneOfManyActors() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );
    provider.allocate( ACTOR_2, asList( ALLOCATABLE_1 ) );

    eventBus.register( captor );
    provider.deallocate( ACTOR_2, asList( ALLOCATABLE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getAllocations() ).contains( ALLOCATABLE_1 );
  }

  @Test
  public void changeAllocationsReturnValue() {
    eventBus.register( captor );
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    provider.getAllocations().add( ALLOCATABLE_2 );
    captor.event.getAllocations().add( ALLOCATABLE_2 );

    assertThat( provider.getAllocations() ).hasSize( 1 );
  }

  @Test
  public void dispose() {
    provider.allocate( ACTOR_1, asList( ALLOCATABLE_1 ) );

    provider.dispose();

    assertThat( provider.getAllocations() ).isEmpty();
  }
}