package com.codeaffine.home.control.engine.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.entity.SensorControlFactoryImpl;
import com.codeaffine.home.control.engine.entity.AllocationTrackerImpl;
import com.codeaffine.home.control.engine.event.EventBusImpl;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControl;

public class SensorControlFactoryImplTest {

  private AllocationTrackerImpl zoneProvider;
  private SensorControlFactoryImpl factory;


  @Before
  public void setUp() {
    zoneProvider = new AllocationTrackerImpl( new EventBusImpl() );
    factory = new SensorControlFactoryImpl( zoneProvider );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void create() {
    Entity<EntityDefinition<?>> expected = mock( Entity.class );

    SensorControl control = factory.create( mock( Entity.class ) );
    control.registerAllocable( expected );
    control.allocate();

    assertThat( zoneProvider.getAllocated() ).contains( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorArgument() {
    factory.create( null );
  }
}