package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.entity.AllocationEventAssert.assertThat;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.engine.entity.AllocationTrackerImpl;
import com.codeaffine.home.control.engine.event.EventBusImpl;
import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.event.Subscribe;

@SuppressWarnings("unchecked")
public class AllocationTrackerImplTest {

  private static final Entity<EntityDefinition<?>> ALLOCABLE_1 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> ALLOCABLE_2 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> ALLOCABLE_3 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> SENSOR_1 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> SENSOR_2 = mock( Entity.class );

  private AllocationTrackerImpl tracker;
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
    tracker = new AllocationTrackerImpl( eventBus );
  }

  @Test
  public void allocate() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    eventBus.register( captor );
    tracker.allocate( SENSOR_2, asSet( ALLOCABLE_2, ALLOCABLE_3 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_2 )
      .hasAllocated( ALLOCABLE_1, ALLOCABLE_2, ALLOCABLE_3 )
      .hasAdditions( ALLOCABLE_2, ALLOCABLE_3 )
      .hasNoRemovals();
    assertThat( tracker.getAllocated() )
      .contains( ALLOCABLE_1, ALLOCABLE_2 );
  }

  @Test
  public void allocateIfAllocatedIsEmpty() {
    eventBus.register( captor );
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_1 )
      .hasAllocated( ALLOCABLE_1 )
      .hasAdditions( ALLOCABLE_1 )
      .hasNoRemovals();
    assertThat( tracker.getAllocated() )
      .contains( ALLOCABLE_1 );
  }

  @Test
  public void allocateIfAllocableIsAlreadyAllocated() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    eventBus.register( captor );
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( tracker.getAllocated() ).contains( ALLOCABLE_1 );
  }

  @Test
  public void allocateSameAllocableByDifferentSensors() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    eventBus.register( captor );
    tracker.allocate( SENSOR_2, asSet( ALLOCABLE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( tracker.getAllocated() ).contains( ALLOCABLE_1 );
  }

  @Test
  public void allocateIfSensorIsWithoutAllocable() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    eventBus.register( captor );
    tracker.allocate( SENSOR_2, emptySet() );

    assertThat( captor.event ).isNull();
    assertThat( tracker.getAllocated() ).contains( ALLOCABLE_1 );
  }

  @Test
  public void release() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );
    tracker.allocate( SENSOR_2, asSet( ALLOCABLE_2, ALLOCABLE_3 ) );

    eventBus.register( captor );
    tracker.release( SENSOR_2, asSet( ALLOCABLE_2, ALLOCABLE_3 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_2 )
      .hasAllocated( ALLOCABLE_1 )
      .hasNoAdditions()
      .hasRemovals( ALLOCABLE_2, ALLOCABLE_3 );
    assertThat( tracker.getAllocated() )
      .contains( ALLOCABLE_1 );
  }

  @Test
  public void releaseLastElement() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    eventBus.register( captor );
    tracker.release( SENSOR_1, asSet( ALLOCABLE_1 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_1 )
      .hasNoAllocated()
      .hasNoAdditions()
      .hasRemovals( ALLOCABLE_1 );
    assertThat( tracker.getAllocated() )
      .isEmpty();
  }

  @Test
  public void releaseIfAllocableIsNotAllocated() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    eventBus.register( captor );
    tracker.release( SENSOR_2, asSet( ALLOCABLE_2 ) );

    assertThat( captor.event ).isNull();
    assertThat( tracker.getAllocated() ).contains( ALLOCABLE_1 );
  }

  @Test
  public void releaseByOneOfManySensors() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );
    tracker.allocate( SENSOR_2, asSet( ALLOCABLE_1 ) );

    eventBus.register( captor );
    tracker.release( SENSOR_2, asSet( ALLOCABLE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( tracker.getAllocated() ).contains( ALLOCABLE_1 );
  }

  @Test
  public void changeGetAllocatedReturnValue() {
    eventBus.register( captor );
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    tracker.getAllocated().add( ALLOCABLE_2 );
    captor.event.getAllocated().add( ALLOCABLE_2 );

    assertThat( tracker.getAllocated() ).hasSize( 1 );
  }

  @Test
  public void dispose() {
    tracker.allocate( SENSOR_1, asSet( ALLOCABLE_1 ) );

    tracker.dispose();

    assertThat( tracker.getAllocated() ).isEmpty();
  }
}