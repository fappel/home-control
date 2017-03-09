package com.codeaffine.home.control.engine.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.event.EventBusImpl;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControl;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

@SuppressWarnings("unchecked")
public class SensorControlImplTest {

  private Entity<EntityDefinition<?>> allocable;
  private AllocationTrackerImpl tracker;
  private SensorControl control;

  @Before
  public void setUp() {
    tracker = new AllocationTrackerImpl( new EventBusImpl() );
    SensorControlFactoryImpl factory = new SensorControlFactoryImpl( tracker );
    control = factory.create( mock( Entity.class ) );
    allocable = mock( Entity.class );
  }

  @Test
  public void allocate() {
    control.registerAllocable( allocable );

    control.allocate();

    assertThat( tracker.getAllocated() ).contains( allocable );
  }

  @Test
  public void allocateWithoutRegisteredAllocable() {
    control.allocate();

    assertThat( tracker.getAllocated() ).isEmpty();
  }

  @Test
  public void registerAllocableAfterAllocate() {
    control.allocate();

    control.registerAllocable( allocable );

    assertThat( tracker.getAllocated() ).contains( allocable );
  }

  @Test
  public void registerAllocableWhichIsAlreadyAllocatedASecondTime() {
    control.registerAllocable( allocable );
    control.allocate();

    control.registerAllocable( allocable );

    assertThat( tracker.getAllocated() ).contains( allocable );
  }

  @Test
  public void release() {
    control.registerAllocable( allocable );
    control.allocate();

    control.release();

    assertThat( tracker.getAllocated() ).isEmpty();
  }

  @Test
  public void unregisterAllocableAfterAllocate() {
    control.registerAllocable( allocable );

    control.allocate();
    control.unregisterAllocable( allocable );

    assertThat( tracker.getAllocated() ).isEmpty();
  }

  @Test
  public void unregisterAllocableAfterRelease() {
    control.registerAllocable( allocable );
    control.allocate();

    control.release();
    control.unregisterAllocable( allocable );

    assertThat( tracker.getAllocated() ).isEmpty();
  }

  @Test
  public void unregisterAllocableTwiceAfterAllocate() {
    control.registerAllocable( allocable );
    control.allocate();

    control.unregisterAllocable( allocable );
    control.unregisterAllocable( allocable );

    assertThat( tracker.getAllocated() ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerAllocableWithNullAsAllocable() {
    control.registerAllocable( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterAllocableWithNullAsAllocable() {
    control.unregisterAllocable( null );
  }
}