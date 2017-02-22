package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.home.control.internal.entity.Sets.asSet;
import static com.codeaffine.home.control.internal.entity.ZoneEventAssert.assertThat;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneEvent;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.internal.event.EventBusImpl;

@SuppressWarnings("unchecked")
public class ZoneProviderImplTest {

  private static final Entity<EntityDefinition<?>> ZONE_1 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> ZONE_2 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> ZONE_3 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> SENSOR_1 = mock( Entity.class );
  private static final Entity<EntityDefinition<?>> SENSOR_2 = mock( Entity.class );

  private ZoneProviderImpl provider;
  private EventBusImpl eventBus;
  private EventCaptor captor;

  static class EventCaptor {

    private ZoneEvent event;

    @Subscribe void captureEvent( ZoneEvent event ) {
      this.event = event;
    }
  }

  @Before
  public void setUp() {
    captor = new EventCaptor();
    eventBus = new EventBusImpl();
    provider = new ZoneProviderImpl( eventBus );
  }

  @Test
  public void engage() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    eventBus.register( captor );
    provider.engage( SENSOR_2, asSet( ZONE_2, ZONE_3 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_2 )
      .hasEngagedZones(  ZONE_1, ZONE_2, ZONE_3 )
      .hasAdditions( ZONE_2, ZONE_3 )
      .hasNoRemovals();
    assertThat( provider.getEngagedZones() )
      .contains( ZONE_1, ZONE_2 );
  }

  @Test
  public void engageIfEngagedZonesAreEmpty() {
    eventBus.register( captor );
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_1 )
      .hasEngagedZones( ZONE_1 )
      .hasAdditions( ZONE_1 )
      .hasNoRemovals();
    assertThat( provider.getEngagedZones() )
      .contains( ZONE_1 );
  }

  @Test
  public void engageIfZoneIsAlreadyEngaged() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    eventBus.register( captor );
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getEngagedZones() ).contains( ZONE_1 );
  }

  @Test
  public void engageSameZoneByDifferentActors() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    eventBus.register( captor );
    provider.engage( SENSOR_2, asSet( ZONE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getEngagedZones() ).contains( ZONE_1 );
  }

  @Test
  public void engageIfSensorIsWithoutZone() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    eventBus.register( captor );
    provider.engage( SENSOR_2, emptySet() );

    assertThat( captor.event ).isNull();
    assertThat( provider.getEngagedZones() ).contains( ZONE_1 );
  }

  @Test
  public void release() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );
    provider.engage( SENSOR_2, asSet( ZONE_2, ZONE_3 ) );

    eventBus.register( captor );
    provider.release( SENSOR_2, asSet( ZONE_2, ZONE_3 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_2 )
      .hasEngagedZones( ZONE_1 )
      .hasNoAdditions()
      .hasRemovals( ZONE_2, ZONE_3 );
    assertThat( provider.getEngagedZones() )
      .contains( ZONE_1 );
  }

  @Test
  public void releaseLastElement() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    eventBus.register( captor );
    provider.release( SENSOR_1, asSet( ZONE_1 ) );

    assertThat( captor.event )
      .hasSensor( SENSOR_1 )
      .hasNoEngagedZones()
      .hasNoAdditions()
      .hasRemovals( ZONE_1 );
    assertThat( provider.getEngagedZones() )
      .isEmpty();
  }

  @Test
  public void releaseIfZoneIsNotEngaged() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    eventBus.register( captor );
    provider.release( SENSOR_2, asSet( ZONE_2 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getEngagedZones() ).contains( ZONE_1 );
  }

  @Test
  public void releaseByOneOfManySensors() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );
    provider.engage( SENSOR_2, asSet( ZONE_1 ) );

    eventBus.register( captor );
    provider.release( SENSOR_2, asSet( ZONE_1 ) );

    assertThat( captor.event ).isNull();
    assertThat( provider.getEngagedZones() ).contains( ZONE_1 );
  }

  @Test
  public void changeEngagedZonesReturnValue() {
    eventBus.register( captor );
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    provider.getEngagedZones().add( ZONE_2 );
    captor.event.getEngagedZones().add( ZONE_2 );

    assertThat( provider.getEngagedZones() ).hasSize( 1 );
  }

  @Test
  public void dispose() {
    provider.engage( SENSOR_1, asSet( ZONE_1 ) );

    provider.dispose();

    assertThat( provider.getEngagedZones() ).isEmpty();
  }
}