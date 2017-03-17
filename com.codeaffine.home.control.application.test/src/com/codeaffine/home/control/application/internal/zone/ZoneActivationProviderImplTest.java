package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.Messages.*;
import static com.codeaffine.home.control.application.internal.zone.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.application.internal.zone.ZoneActivationProviderImpl.*;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static com.codeaffine.home.control.test.util.entity.SensorHelper.stubSensor;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorEvent;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;

public class ZoneActivationProviderImplTest {

  private static final EntityDefinition<?> ZONE_DEFINITION_1 = stubEntityDefinition( "Zone1" );
  private static final EntityDefinition<?> ZONE_DEFINITION_2 = stubEntityDefinition( "Zone2" );
  private static final EntityDefinition<?> ZONE_DEFINITION_3 = stubEntityDefinition( "Zone3" );
  private static final Entity<EntityDefinition<?>> ZONE_1 = stubEntity( ZONE_DEFINITION_1 );
  private static final Entity<EntityDefinition<?>> ZONE_2 = stubEntity( ZONE_DEFINITION_2 );
  private static final Entity<EntityDefinition<?>> ZONE_3 = stubEntity( ZONE_DEFINITION_3 );

  private ZoneActivationProviderImpl provider;
  private AdjacencyDefinition adjacency;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    adjacency = new AdjacencyDefinition( asSet( ZONE_DEFINITION_1, ZONE_DEFINITION_2, ZONE_DEFINITION_3 ) );
    adjacency.link( ZONE_DEFINITION_1, ZONE_DEFINITION_2 ).link( ZONE_DEFINITION_2, ZONE_DEFINITION_3 );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    provider = new ZoneActivationProviderImpl( adjacency, eventBus, logger );
  }

  @Test
  public void engageZonesChanged() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );

    ArgumentCaptor<StatusEvent> captor = forClass( StatusEvent.class );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource( ZoneActivationProvider.class ) ).hasValue( provider );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1 ]" ) );
  }

  @Test
  public void getStatusAfterZoneEngaging() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.iterator().next().getReleaseTime() ).isEmpty();
  }

  @Test
  public void getStatusAfterSubsequentZoneEngaging() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1, Zone2 ]" ) );
  }

  @Test
  public void getStatusAfterReleaseOfFirstEngaging() {
    Entity<EntityDefinition<?>> expected = ZONE_2;

    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, expected ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( expected ) );
  }

  @Test
  public void getStatusAfterReleaseOfSecondEngaging() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1 ) );
  }

  @Test
  public void getStatusAfterReleaseOfTheOnlyActiveZone() {
    Entity<EntityDefinition<?>> expected = ZONE_1;

    provider.engagedZonesChanged( newEvent( ON, expected ) );
    reset( logger, eventBus );
    provider.engagedZonesChanged( newEvent( OFF, expected ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( expected ) );
    assertThat( actual.iterator().next().getReleaseTime() ).isNotEmpty();
    ArgumentCaptor<String> loggingCaptor = forClass( String.class );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), loggingCaptor.capture() );
    assertThat( loggingCaptor.getValue() ).contains( ZONE_1.toString(), RELEASED_TAG );
    ArgumentCaptor<StatusEvent> event = forClass( StatusEvent.class );
    verify( eventBus ).post( event.capture() );
    assertThat( event.getValue().getSource( ZoneActivationProvider.class ) ).hasValue( provider );
  }

  @Test
  public void getStatusAfterRemovalOfNonAdjacentZoneEngagings() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    ArgumentCaptor<String> captor = forClass( String.class );
    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    InOrder order = inOrder( logger );
    order.verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1 ]" ) );
    order.verify( logger, times( 2 ) ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), captor.capture() );
    order.verifyNoMoreInteractions();
    assertThat( asList( captor.getValue().split( "\\|" ) ) )
      .allMatch( zone -> zone.contains( RELEASED_TAG ) && zone.contains( "Zone1" ) || zone.contains( "Zone3" ) )
      .hasSize( 2 );
  }

  @Test
  public void getStatusAfterMovementInNonAdjacentZoneEngagings() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
  }

  @Test
  public void getStatusJoiningFromMultipleZoneEngagings() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1 ) );
  }

  @Test
  public void getStatusOnInPathRelease() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2, ZONE_3 ) );
  }

  @Test
  public void getStatusOnFirstEndPointReleaseAfterInPathRelease() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2, ZONE_3 ) );
  }

  @Test
  public void getStatusOnInPathReleaseWithReactivatedInPathRelease() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_2 ) );
  }

  @Test
  public void getStatusOnSecondEndPointReleaseAfterInPathRelease() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    reset( logger );

    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).info( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 <released> ]" );
  }

  @Test
  public void getStatusAfterAdjacentZoneEngagingOfExistingReleasedZone() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    reset( logger );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    ArgumentCaptor<String> captor = forClass( String.class );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), captor.capture() );
    assertThat( asList( captor.getValue().split( "\\|" ) ) )
      .allMatch( zone -> zone.contains( RELEASED_TAG ) && zone.contains( "Zone1" ) || zone.contains( "Zone2" ) )
      .hasSize( 2 );
  }

  @Test
  public void releaseTimeoutsOfInPathReleases() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );

    reset( logger );
    provider.setTimeSupplier( () -> now().plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME + 1 ) );
    provider.releaseTimeouts();
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    ArgumentCaptor<String> captor = forClass( String.class );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), captor.capture() );
    assertThat( asList( captor.getValue().split( "\\|" ) ) )
      .allMatch( zone -> zone.contains( "Zone1" ) || zone.contains( "Zone3" ) )
      .hasSize( 2 );
  }

  @Test
  @SuppressWarnings( "cast" )
  public void releaseTimeoutsOfInPathReleasesWithMultipleZoneEngagingsWithoutExpiredZones() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );

    reset( logger );
    provider.setTimeSupplier( () -> now().plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME + 1 ) );
    provider.releaseTimeouts();
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    verify( logger, never() ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), ( Object )anyObject() );
  }

  @Test
  public void releaseTimeoutsOfInPathReleasesOnFirstEndPointReleaseAfterInPathRelease() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );

    reset( logger );
    provider.setTimeSupplier( () -> now().plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME + 1 ) );
    provider.releaseTimeouts();
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).info( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 ]" );
  }

  @Test
  public void releaseTimeoutsOnPotentiallyDeadTrace() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    reset( logger );
    provider.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );
    provider.releaseTimeouts();
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_3 ) );
    verify( logger ).info( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone3 ]" );
  }

  @Test
  public void releaseTimeoutsOnMoreThanOnePotentiallyDeadTrace() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    waitALittle();
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    reset( logger );
    provider.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );
    provider.releaseTimeouts();
    Set<ZoneActivation> actual = provider.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).info( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 <released> ]" );
  }

  @Test
  public void engageZonesChangedWithOffIfNoZoneActivationExistsYet() {
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    assertThat( provider.getStatus() ).isEmpty();
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdjacencyDefinitionArgument() {
    new ZoneActivationProviderImpl( null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusDefinitionArgument() {
    new ZoneActivationProviderImpl( adjacency, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerDefinitionArgument() {
    new ZoneActivationProviderImpl( adjacency, eventBus, null );
  }

  @SafeVarargs
  private static Set<Entity<EntityDefinition<?>>> $( Entity<EntityDefinition<?>> ...zones ) {
    return asSet( zones );
  }

  @SafeVarargs
  private static <T> Set<T> asSet( T ... elements ) {
    return new HashSet<>( asList( elements ) );
  }

  @SafeVarargs
  private static MotionSensorEvent newEvent( OnOff sensorStatus, Entity<EntityDefinition<?>> ... affected ) {
    return new MotionSensorEvent( stubSensor( "sensor" ), sensorStatus, affected );
  }

  private static Set<Entity<?>> toZoneSet( Set<ZoneActivation> zoneActivations ) {
    return zoneActivations.stream().map( activation -> activation.getZone() ).collect( toSet() );
  }
}