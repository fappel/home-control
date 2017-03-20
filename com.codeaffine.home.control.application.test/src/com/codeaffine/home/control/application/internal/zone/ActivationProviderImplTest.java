package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.ActivationProviderImpl.*;
import static com.codeaffine.home.control.application.internal.zone.Messages.*;
import static com.codeaffine.home.control.application.internal.zone.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.entity.SensorHelper.stubSensor;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorEvent;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;

public class ActivationProviderImplTest {

  private ActivationProviderImpl provider;
  private AdjacencyDefinition adjacency;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    adjacency = new AdjacencyDefinition( asSet( ZONE_DEFINITION_1, ZONE_DEFINITION_2, ZONE_DEFINITION_3 ) );
    adjacency.link( ZONE_DEFINITION_1, ZONE_DEFINITION_2 ).link( ZONE_DEFINITION_2, ZONE_DEFINITION_3 );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    provider = new ActivationProviderImpl( adjacency, eventBus, logger );
  }

  @Test
  public void engageZonesChanged() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );

    verifyEventBusNotification();
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1 ]" ) );
  }

  @Test
  public void getStatusAfterZoneEngaging() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.getReleaseTime().isPresent() );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
  }

  @Test
  public void getStatusAfterSubsequentZoneEngaging() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1, Zone2 ]" ) );
  }

  @Test
  public void getStatusAfterReleaseOfFirstEngaging() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_2 ) );
  }

  @Test
  public void getStatusAfterReleaseOfSecondEngaging() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
  }

  @Test
  public void getStatusAfterReleaseOfTheOnlyActiveZone() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    reset( logger, eventBus );

    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.getAllZones() )
      .allMatch( zone -> !zone.isAdjacentActivated() )
      .allMatch( zone -> zone.getReleaseTime().isPresent() );
    assertThat( captureLoggerInfoArgument() ).contains( ZONE_1.toString(), RELEASED_TAG );
    verifyEventBusNotification();
  }

  @Test
  public void getStatusAfterReactivationOfTheOnlyReleasedZone() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    reset( logger, eventBus );

    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.getAllZones() )
      .allMatch( zone -> !zone.isAdjacentActivated() )
      .allMatch( zone -> !zone.getReleaseTime().isPresent() );
    assertThat( captureLoggerInfoArgument() ).isEqualTo( "[ Zone1 ]" );
    verifyEventBusNotification();
  }

  @Test
  public void getStatusAfterRemovalOfNonAdjacentZoneEngagings() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );

    reset( logger );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
    assertThat( asList( captureLoggerInfoArgument().split( "\\|" ) ) )
      .allMatch( info -> !info.contains( "," ) )
      .allMatch( info -> info.contains( RELEASED_TAG ) && info.contains( "Zone1" ) || info.contains( "Zone3" ) )
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
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
    reset( logger );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( asList( captureLoggerInfoArgument().split( "\\|" ) ) ).hasSize( 1 );
  }

  @Test
  public void getStatusOnInPathRelease() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
  }

  @Test
  public void getStatusOnFirstEndPointReleaseAfterInPathRelease() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_2 ) );
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).info( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 <released> ]" );
  }

  @Test
  public void getStatusAfterAdjacentZoneEngagingOfExistingReleasedZone() {
    provider.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    reset( logger );
    provider.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( activation -> activation.isAdjacentActivated() );
    assertThat( asList( captureLoggerInfoArgument().split( "\\|" ) ) )
      .allMatch( info -> !info.contains( "," ) )
      .allMatch( info -> info.contains( RELEASED_TAG ) && info.contains( "Zone1" ) || info.contains( "Zone2" ) )
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
    assertThat( asList( captureLoggerInfoArgument().split( "\\|" ) ) )
      .allMatch( info -> !info.contains( "," ) )
      .allMatch( info -> info.contains( "Zone1" ) || info.contains( "Zone3" ) )
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( activation -> activation.isAdjacentActivated() );
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_3 ) );
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
    Activation actual = provider.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).info( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 <released> ]" );
  }

  @Test
  public void engageZonesChangedWithOffIfNoZoneActivationExistsYet() {
    provider.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    assertThat( provider.getStatus().getAllZones() ).isEmpty();
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdjacencyDefinitionArgument() {
    new ActivationProviderImpl( null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusDefinitionArgument() {
    new ActivationProviderImpl( adjacency, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerDefinitionArgument() {
    new ActivationProviderImpl( adjacency, eventBus, null );
  }

  private void verifyEventBusNotification() {
    ArgumentCaptor<StatusEvent> event = forClass( StatusEvent.class );
    verify( eventBus ).post( event.capture() );
    assertThat( event.getValue().getSource( ActivationProvider.class ) ).hasValue( provider );
  }

  private String captureLoggerInfoArgument() {
    ArgumentCaptor<String> captor = forClass( String.class );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), captor.capture() );
    return captor.getValue();
  }

  @SafeVarargs
  private static Set<Entity<EntityDefinition<?>>> $( Entity<EntityDefinition<?>> ...zones ) {
    return asSet( zones );
  }

  @SafeVarargs
  private static MotionSensorEvent newEvent( OnOff sensorStatus, Entity<EntityDefinition<?>> ... affected ) {
    return new MotionSensorEvent( stubSensor( "sensor" ), sensorStatus, affected );
  }

  private static Set<Entity<?>> toZoneEntitySet( Activation activation ) {
    return activation.getAllZones().stream().map( zone -> zone.getZoneEntity() ).collect( toSet() );
  }
}