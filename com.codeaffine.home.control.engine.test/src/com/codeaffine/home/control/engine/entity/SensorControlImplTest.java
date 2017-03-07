package com.codeaffine.home.control.engine.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;
import com.codeaffine.home.control.engine.entity.SensorControlFactoryImpl;
import com.codeaffine.home.control.engine.entity.ZoneProviderImpl;
import com.codeaffine.home.control.engine.event.EventBusImpl;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

@SuppressWarnings("unchecked")
public class SensorControlImplTest {

  private Entity<EntityDefinition<?>> zone;
  private ZoneProviderImpl zoneProvider;
  private SensorControl control;

  @Before
  public void setUp() {
    zoneProvider = new ZoneProviderImpl( new EventBusImpl() );
    SensorControlFactoryImpl factory = new SensorControlFactoryImpl( zoneProvider );
    control = factory.create( mock( Entity.class ) );
    zone = mock( Entity.class );
  }

  @Test
  public void engage() {
    control.registerZone( zone );

    control.engage();

    assertThat( zoneProvider.getEngagedZones() ).contains( zone );
  }

  @Test
  public void engageWithoutRegisteredZone() {
    control.engage();

    assertThat( zoneProvider.getEngagedZones() ).isEmpty();
  }

  @Test
  public void registerZoneAfterEngage() {
    control.engage();

    control.registerZone( zone );

    assertThat( zoneProvider.getEngagedZones() ).contains( zone );
  }

  @Test
  public void registerZoneAfterEngageAndAlreadyEngaged() {
    control.registerZone( zone );
    control.engage();

    control.registerZone( zone );

    assertThat( zoneProvider.getEngagedZones() ).contains( zone );
  }

  @Test
  public void release() {
    control.registerZone( zone );
    control.engage();

    control.release();

    assertThat( zoneProvider.getEngagedZones() ).isEmpty();
  }

  @Test
  public void unregisterZoneAfterEngage() {
    control.registerZone( zone );

    control.engage();
    control.unregisterZone( zone );

    assertThat( zoneProvider.getEngagedZones() ).isEmpty();
  }

  @Test
  public void unregisterZoneAfterRelease() {
    control.registerZone( zone );
    control.engage();

    control.release();
    control.unregisterZone( zone );

    assertThat( zoneProvider.getEngagedZones() ).isEmpty();
  }

  @Test
  public void unregisterZoneTwiceAfterEngage() {
    control.registerZone( zone );
    control.engage();

    control.unregisterZone( zone );
    control.unregisterZone( zone );

    assertThat( zoneProvider.getEngagedZones() ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerZoneWithNullAsZone() {
    control.registerZone( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterZoneWithNullAsZone() {
    control.unregisterZone( null );
  }
}