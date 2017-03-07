package com.codeaffine.home.control.internal.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;
import com.codeaffine.home.control.internal.event.EventBusImpl;

public class SensorControlFactoryImplTest {

  private ZoneProviderImpl zoneProvider;
  private SensorControlFactoryImpl factory;


  @Before
  public void setUp() {
    zoneProvider = new ZoneProviderImpl( new EventBusImpl() );
    factory = new SensorControlFactoryImpl( zoneProvider );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void create() {
    Entity<EntityDefinition<?>> expected = mock( Entity.class );

    SensorControl control = factory.create( mock( Entity.class ) );
    control.registerZone( expected );
    control.engage();

    assertThat( zoneProvider.getEngagedZones() ).contains( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorArgument() {
    factory.create( null );
  }
}