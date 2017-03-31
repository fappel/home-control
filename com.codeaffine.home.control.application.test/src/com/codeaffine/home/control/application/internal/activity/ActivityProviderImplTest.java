package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.ActivityProviderImpl.*;
import static com.codeaffine.home.control.application.internal.activity.Messages.INFO_ACTIVITY_RATE;
import static com.codeaffine.home.control.application.internal.activity.Util.calculateMaxActivations;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition.*;
import static com.codeaffine.home.control.application.test.ActivationHelper.createZone;
import static com.codeaffine.home.control.application.test.ActivityAssert.assertThat;
import static com.codeaffine.home.control.application.test.EventBusHelper.captureEvent;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;

public class ActivityProviderImplTest {

  private static final BigDecimal MAX_ACTIVATIONS
    = calculateMaxActivations( OBSERVATION_TIME, CALCULATION_INTERVAL );

  private ActivationProvider activationProvider;
  private ActivationSensor bathRoomSensor;
  private ActivityProviderImpl activityProvider;
  private ActivationSensor bedSensor;
  private EventBus eventBus;
  private Section bathroom;
  private Logger logger;
  private Section bed;

  @Before
  public void setUp() {
    bed = stubSection( BED );
    bathroom = stubSection( BATH_ROOM );
    Section livingRoom = stubSection( LIVING_ROOM );
    bedSensor = stubActivationSensor( BED_MOTION );
    bathRoomSensor = stubActivationSensor( BATH_ROOM_MOTION );
    equipWithActivationSensor( bed, bedSensor );
    equipWithActivationSensor( bathroom, bathRoomSensor );
    EntityRegistry registry
      = stubRegistry( asList( bed, bathroom, livingRoom ), emptySet(), asList( bedSensor, bathRoomSensor ) );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    activationProvider = stubEmptyActivationProvider();
    activityProvider = new ActivityProviderImpl( activationProvider, registry, eventBus, logger );
  }

  @Test
  public void calculateRate() {
    int dryRun = captureActivations( MAX_ACTIVATIONS.intValue() / 2 );
    stubActivationSensorAsEngaged( bedSensor );
    stubActivationProvider( BED );
    captureActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubActivationSensorAsReleased( bedSensor );
    stubActivationSensorAsEngaged( bathRoomSensor );
    stubActivationProvider( BATH_ROOM );
    captureActivations( MAX_ACTIVATIONS.intValue() / 6 );

    activityProvider.calculateRate();
    Activity actual = activityProvider.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_050 )
      .hasSectionActivity( BED, P_017 )
      .hasSectionActivity( BATH_ROOM, P_017 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_017 )
      .hasSectionAllocation( BATH_ROOM, P_017 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
    assertThat( dryRun ).isGreaterThan( 0 );
    verifyEventNotification();
  }

  @Test
  public void calculateRateOnMaximumOfExpectedActivations() {
    stubActivationSensorAsEngaged( bedSensor );
    stubActivationProvider( BED );
    captureActivations( MAX_ACTIVATIONS.intValue()  );

    activityProvider.calculateRate();
    Activity actual = activityProvider.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_100 )
      .hasSectionActivity( BED, P_100 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_100 )
      .hasSectionAllocation( BATH_ROOM, P_000 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
    verifyEventNotification();
  }

  @Test
  public void calculateRateOnExpectedActivationOverflow() {
    stubActivationSensorAsEngaged( bedSensor );
    stubActivationProvider( BED );
    captureActivations( MAX_ACTIVATIONS.intValue() + 1 );

    activityProvider.calculateRate();
    Activity actual = activityProvider.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_100 )
      .hasSectionActivity( BED, P_100 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_100 )
      .hasSectionAllocation( BATH_ROOM, P_000 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
    verifyEventNotification();
  }

  @Test
  public void calculateRateAfterCreation() {
    activityProvider.calculateRate();

    Activity actual = activityProvider.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_000 )
      .hasSectionActivity( BED, P_000 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_000 )
      .hasSectionAllocation( BATH_ROOM, P_000 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
    verify( logger, never() ).info( anyString(), eq( actual ) );
  }

  @Test
  public void calculateRateIfNoActivationHasBeenCaptured() {
    int dryRun = captureActivations( MAX_ACTIVATIONS.intValue() / 2 );

    activityProvider.calculateRate();
    Activity actual = activityProvider.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_000 )
      .hasSectionActivity( BED, P_000 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_000 )
      .hasSectionAllocation( BATH_ROOM, P_000 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
    assertThat( dryRun ).isGreaterThan( 0 );
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
    verify( logger, never() ).info( anyString(), eq( actual ) );
  }

  @Test
  public void calculateRateWithExpiredTimestamps() {
    int dryRun = captureActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubActivationSensorAsEngaged( bedSensor );
    stubActivationProvider( BED );
    activityProvider.setTimestampSupplier( () -> now().minusMinutes( ActivityProviderImpl.OBSERVATION_TIME + 1 ) );
    captureActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubActivationSensorAsReleased( bedSensor );
    stubActivationSensorAsEngaged( bathRoomSensor );
    stubActivationProvider( BATH_ROOM );
    activityProvider.setTimestampSupplier( () -> now() );
    captureActivations( MAX_ACTIVATIONS.intValue() / 3 );

    activityProvider.calculateRate();
    Activity actual = activityProvider.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_033 )
      .hasSectionActivity( BED, P_000 )
      .hasSectionActivity( BATH_ROOM, P_033 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_000 )
      .hasSectionAllocation( BATH_ROOM, P_033 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
    assertThat( dryRun ).isGreaterThan( 0 );
    verifyEventNotification();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationProviderArgument() {
    new ActivityProviderImpl( null, mock( EntityRegistry.class ), eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRegistryArgument() {
    new ActivityProviderImpl( activationProvider, null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new ActivityProviderImpl( activationProvider, mock( EntityRegistry.class ), null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new ActivityProviderImpl( activationProvider, mock( EntityRegistry.class ), eventBus, null );
  }

  private void verifyEventNotification() {
    assertThat( captureEvent( eventBus, ActivityProvider.class ) ).hasValue( activityProvider );
    verify( logger ).info( INFO_ACTIVITY_RATE, activityProvider.getStatus() );
  }

  private static void stubActivationSensorAsEngaged( ActivationSensor sensor ) {
    when( sensor.isEngaged() ).thenReturn( true );
  }

  private static void stubActivationSensorAsReleased( ActivationSensor sensor ) {
    when( sensor.isEngaged() ).thenReturn( false );
  }

  private void stubActivationProvider( SectionDefinition ... definitions ) {
    Set<Zone> zones = Stream.of( definitions ).map( def -> createZone( stubEntity( def ) ) ).collect( toSet() );
    when( activationProvider.getStatus() ).thenReturn( new Activation( zones ) );
  }

  private static ActivationProvider stubEmptyActivationProvider() {
    ActivationProvider result = mock( ActivationProvider.class );
    when( result.getStatus() ).thenReturn( new Activation( emptySet() ) );
    return result;
  }

  private int captureActivations( int times ) {
    asList( new Object[ times ] ).forEach( nix -> activityProvider.captureActivations() );
    return times;
  }
}