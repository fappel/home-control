package com.codeaffine.home.control.status.internal.activation;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.status.internal.activation.ActivationSupplierImpl.*;
import static com.codeaffine.home.control.status.internal.activation.Messages.*;
import static com.codeaffine.home.control.status.internal.activation.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.*;
import static com.codeaffine.home.control.status.type.OnOff.*;
import static com.codeaffine.home.control.test.util.entity.SensorHelper.stubSensor;
import static com.codeaffine.home.control.test.util.logger.LoggerHelper.captureSingleDebugArgument;
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

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.model.ActivationEvent;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.AdjacencyDefinition;
import com.codeaffine.home.control.status.type.OnOff;

public class ActivationSupplierImplTest {

  private ActivationSupplierImpl supplier;
  private AdjacencyDefinition adjacency;
  private EventBus eventBus;
  private Logger logger;
  private Sensor sensor;

  @Before
  public void setUp() {
    sensor = stubSensor( "sensor" );
    adjacency = new AdjacencyDefinition( asSet( ZONE_DEFINITION_1, ZONE_DEFINITION_2, ZONE_DEFINITION_3 ) );
    adjacency.link( ZONE_DEFINITION_1, ZONE_DEFINITION_2 ).link( ZONE_DEFINITION_2, ZONE_DEFINITION_3 );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    supplier = new ActivationSupplierImpl( adjacency, eventBus, logger );
  }

  @Test
  public void engageZonesChanged() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );

    verifyEventBusNotification();
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 ]" );
  }

  @Test
  public void getStatusAfterZoneEngaging() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.getReleaseTime().isPresent() );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
  }

  @Test
  public void getStatusAfterSubsequentZoneEngaging() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1, Zone2 ]" );
  }

  @Test
  public void getStatusAfterReleaseOfFirstEngaging() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_2 ) );
  }

  @Test
  public void getStatusAfterReleaseOfSecondEngaging() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
  }

  @Test
  public void getStatusAfterReleaseOfTheOnlyActiveZone() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    reset( logger, eventBus );

    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.getAllZones() )
      .allMatch( zone -> !zone.isAdjacentActivated() )
      .allMatch( zone -> zone.getReleaseTime().isPresent() );
    assertThat( captureLoggerDebugArgument() ).contains( ZONE_1.toString(), RELEASED_TAG );
    verifyEventBusNotification();
  }

  @Test
  public void getStatusAfterReactivationOfTheOnlyReleasedZone() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    reset( logger, eventBus );

    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.getAllZones() )
      .allMatch( zone -> !zone.isAdjacentActivated() )
      .allMatch( zone -> !zone.getReleaseTime().isPresent() );
    assertThat( captureLoggerDebugArgument() ).isEqualTo( "[ Zone1 ]" );
    verifyEventBusNotification();
  }

  @Test
  public void getStatusAfterRemovalOfNonAdjacentZoneEngagings() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );

    reset( logger );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
    assertThat( asList( captureLoggerDebugArgument().split( "\\|" ) ) )
      .allMatch( info -> !info.contains( "," ) )
      .allMatch( info -> info.contains( RELEASED_TAG ) && info.contains( "Zone1" ) || info.contains( "Zone3" ) )
      .hasSize( 2 );
  }

  @Test
  public void getStatusAfterMovementInNonAdjacentZoneEngagings() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
  }

  @Test
  public void getStatusJoiningFromMultipleZoneEngagings() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    reset( logger );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( asList( captureLoggerDebugArgument().split( "\\|" ) ) ).hasSize( 1 );
  }

  @Test
  public void getStatusOnInPathRelease() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
  }

  @Test
  public void getStatusOnFirstEndPointReleaseAfterInPathRelease() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
  }

  @Test
  public void getStatusOnInPathReleaseWithReactivatedInPathRelease() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_2 ) );
  }

  @Test
  public void getStatusOnSecondEndPointReleaseAfterInPathRelease() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    reset( logger );

    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 <released> ]" );
  }

  @Test
  public void getStatusAfterAdjacentZoneEngagingOfExistingReleasedZone() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    reset( logger );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( activation -> activation.isAdjacentActivated() );
    assertThat( asList( captureLoggerDebugArgument().split( "\\|" ) ) )
      .allMatch( info -> !info.contains( "," ) )
      .allMatch( info -> info.contains( RELEASED_TAG ) && info.contains( "Zone1" ) || info.contains( "Zone2" ) )
      .hasSize( 2 );
  }

  @Test
  public void getStatusAfterEngagingTheSameZoneMoreThanOnce() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 ]" );
  }

  @Test
  public void getStatusAfterReleasingSameZoneMoreThanOnce() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone2 ]" );
  }

  @Test
  public void getStatusAfterSingleReleaseOfMultiSensorEngagment() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( stubSensor( "secondSensor" ), ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> zone.isAdjacentActivated() );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1, Zone2 ]" );
  }

  @Test
  public void getStatusAfterMultiSensorEngagementInCaseOfMultiplePaths() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );

    reset( logger );
    supplier.engagedZonesChanged( newEvent( stubSensor( "otherSensor" ), ON, ZONE_3 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
    assertThat( asList( captureLoggerDebugArgument().split( "\\|" ) ) )
      .allMatch( info -> !info.contains( "," ) )
      .allMatch( info -> !info.contains( RELEASED_TAG ) )
      .allMatch( info -> info.contains( "Zone1" ) || info.contains( "Zone3" ) )
      .hasSize( 2 );
  }

  @Test
  public void getStatusAfterCompleteReleaseOfMultiSensorEngagment() {
    Sensor secondSensor = stubSensor( "secondSensor" );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( secondSensor, ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( secondSensor, OFF, ZONE_1 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_2 ) );
  }

  @Test
  public void getStatusAfterReleaseOfZoneThatIsNotEngaged() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
  }

  @Test
  public void getStatusAfterReleaseOfZoneThatIsEngagedByNonRelatedSensor() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( stubSensor( "otherSensor" ), OFF, ZONE_2 ) );
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
  }

  @Test
  public void releaseTimeoutsOfInPathReleases() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );

    reset( logger );
    supplier.setTimeSupplier( () -> now().plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME + 1 ) );
    supplier.releaseTimeouts();
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    assertThat( actual.getAllZones() ).allMatch( zone -> !zone.isAdjacentActivated() );
    assertThat( asList( captureLoggerDebugArgument().split( "\\|" ) ) )
      .allMatch( info -> !info.contains( "," ) )
      .allMatch( info -> info.contains( "Zone1" ) || info.contains( "Zone3" ) )
      .hasSize( 2 );
  }

  @Test
  @SuppressWarnings( "cast" )
  public void releaseTimeoutsOfInPathReleasesWithMultipleZoneEngagingsWithoutExpiredZones() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );

    reset( logger );
    supplier.setTimeSupplier( () -> now().plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME + 1 ) );
    supplier.releaseTimeouts();
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    assertThat( actual.getAllZones() ).allMatch( activation -> activation.isAdjacentActivated() );
    verify( logger, never() ).debug( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), ( Object )anyObject() );
  }

  @Test
  public void releaseTimeoutsOfInPathReleasesOnFirstEndPointReleaseAfterInPathRelease() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );

    reset( logger );
    supplier.setTimeSupplier( () -> now().plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME + 1 ) );
    supplier.releaseTimeouts();
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 ]" );
  }

  @Test
  public void releaseTimeoutsOnPotentiallyDeadTrace() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    reset( logger );
    supplier.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );
    supplier.releaseTimeouts();
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_3 ) );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone3 ]" );
  }

  @Test
  public void releaseTimeoutsOnMoreThanOnePotentiallyDeadTrace() {
    supplier.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    supplier.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    waitALittle();
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    reset( logger );
    supplier.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );
    supplier.releaseTimeouts();
    Activation actual = supplier.getStatus();

    assertThat( toZoneEntitySet( actual ) ).isEqualTo( $( ZONE_1 ) );
    verify( logger ).debug( ZONE_ACTIVATION_STATUS_CHANGED_INFO, "[ Zone1 <released> ]" );
  }

  @Test
  public void engageZonesChangedWithOffIfNoZoneActivationExistsYet() {
    supplier.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );

    assertThat( supplier.getStatus().getAllZones() ).isEmpty();
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdjacencyDefinitionArgument() {
    new ActivationSupplierImpl( null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusDefinitionArgument() {
    new ActivationSupplierImpl( adjacency, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerDefinitionArgument() {
    new ActivationSupplierImpl( adjacency, eventBus, null );
  }

  private void verifyEventBusNotification() {
    ArgumentCaptor<StatusEvent> event = forClass( StatusEvent.class );
    verify( eventBus ).post( event.capture() );
    assertThat( event.getValue().getSource( ActivationSupplier.class ) ).hasValue( supplier );
  }

  private String captureLoggerDebugArgument() {
    return ( String )captureSingleDebugArgument( logger, ZONE_ACTIVATION_STATUS_CHANGED_INFO );
  }

  @SafeVarargs
  private static Set<Entity<EntityDefinition<?>>> $( Entity<EntityDefinition<?>> ...zones ) {
    return asSet( zones );
  }

  @SafeVarargs
  private final ActivationEvent newEvent( OnOff sensorStatus, Entity<EntityDefinition<?>> ... affected ) {
    return newEvent( sensor, sensorStatus, affected );
  }

  @SafeVarargs
  private static ActivationEvent newEvent(
    Sensor sensor, OnOff sensorStatus, Entity<EntityDefinition<?>> ... affected )
  {
    return new ActivationEvent( sensor, sensorStatus, affected );
  }

  private static Set<Entity<?>> toZoneEntitySet( Activation activation ) {
    return activation.getAllZones().stream().map( zone -> zone.getZoneEntity() ).collect( toSet() );
  }
}