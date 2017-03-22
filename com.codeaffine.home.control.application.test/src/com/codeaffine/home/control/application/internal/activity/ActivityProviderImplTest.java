package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.ActivityProviderImpl.*;
import static com.codeaffine.home.control.application.internal.activity.Messages.INFO_ACTIVITY_RATE;
import static com.codeaffine.home.control.application.internal.activity.Util.calculateMaxActivations;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensorDefinition.*;
import static com.codeaffine.home.control.application.test.ActivityAssert.assertThat;
import static com.codeaffine.home.control.application.test.EventBusHelper.captureEvent;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;

public class ActivityProviderImplTest {

  private static final BigDecimal MAX_ACTIVATIONS
    = calculateMaxActivations( OBSERVATION_TIME_FRAME, CALCULATION_INTERVAL );

  private ActivityProviderImpl activity;
  private MotionSensor bedSensor;
  private MotionSensor bathRoomSensor;
  private EventBus eventBus;
  private Section bed;
  private Section bathroom;
  private Logger logger;

  @Before
  public void setUp() {
    bed = stubSection( BED );
    bathroom = stubSection( BATH_ROOM );
    Section section3 = stubSection( LIVING_ROOM );
    bedSensor = stubMotionSensor( BED_MOTION );
    bathRoomSensor = stubMotionSensor( BATH_ROOM_MOTION );
    equipWithMotionSensor( bed, bedSensor );
    equipWithMotionSensor( bathroom, bathRoomSensor );
    EntityRegistry registry
      = stubRegistry( asList( bed, bathroom, section3 ), emptySet(), asList( bedSensor, bathRoomSensor ) );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    activity = new ActivityProviderImpl( registry, eventBus, logger );
  }

  @Test
  public void calculateRate() {
    int dryRun = captureMotionActivations( MAX_ACTIVATIONS.intValue() / 2 );
    stubMotionSensorAsEngaged( bedSensor );
    captureMotionActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubMotionSensorAsReleased( bedSensor );
    stubMotionSensorAsEngaged( bathRoomSensor );
    captureMotionActivations( MAX_ACTIVATIONS.intValue() / 6 );

    activity.calculateRate();
    Activity actual = activity.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_050 )
      .hasSectionActivity( BED, P_033 )
      .hasSectionActivity( BATH_ROOM, P_017 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM );
    assertThat( dryRun ).isGreaterThan( 0 );
    verifyEventNotification();
  }

  @Test
  public void calculateRateOnMaximumOfExpectedActivations() {
    stubMotionSensorAsEngaged( bedSensor );
    captureMotionActivations( MAX_ACTIVATIONS.intValue()  );

    activity.calculateRate();
    Activity actual = activity.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_100 )
      .hasSectionActivity( BED, P_100 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM );
    verifyEventNotification();
  }

  @Test
  public void calculateRateOnExpectedActivationOverflow() {
    stubMotionSensorAsEngaged( bedSensor );
    captureMotionActivations( MAX_ACTIVATIONS.intValue() + 1 );

    activity.calculateRate();
    Activity actual = activity.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_100 )
      .hasSectionActivity( BED, P_100 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM );
    verifyEventNotification();
  }

  @Test
  public void calculateRateAfterCreation() {
    activity.calculateRate();

    Activity actual = activity.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_000 )
      .hasSectionActivity( BED, P_000 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM );
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
    verify( logger, never() ).info( anyString(), eq( actual ) );
  }

  @Test
  public void calculateRateIfNoMotionActivationHasBeenCaptured() {
    int dryRun = captureMotionActivations( MAX_ACTIVATIONS.intValue() / 2 );

    activity.calculateRate();
    Activity actual = activity.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_000 )
      .hasSectionActivity( BED, P_000 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM );
    assertThat( dryRun ).isGreaterThan( 0 );
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
    verify( logger, never() ).info( anyString(), eq( actual ) );
  }

  @Test
  public void calculateRateWithExpiredTimestamps() {
    int dryRun = captureMotionActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubMotionSensorAsEngaged( bedSensor );
    activity.setTimestampSupplier( () -> now().minusMinutes( ActivityProviderImpl.OBSERVATION_TIME_FRAME + 1 ) );
    captureMotionActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubMotionSensorAsReleased( bedSensor );
    stubMotionSensorAsEngaged( bathRoomSensor );
    activity.setTimestampSupplier( () -> now() );
    captureMotionActivations( MAX_ACTIVATIONS.intValue() / 3 );

    activity.calculateRate();
    Activity actual = activity.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_033 )
      .hasSectionActivity( BED, P_000 )
      .hasSectionActivity( BATH_ROOM, P_033 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM );
    assertThat( dryRun ).isGreaterThan( 0 );
    verifyEventNotification();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRegistryArgument() {
    new ActivityProviderImpl( null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new ActivityProviderImpl( mock( EntityRegistry.class ), null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new ActivityProviderImpl( mock( EntityRegistry.class ), eventBus, null );
  }

  private void verifyEventNotification() {
    assertThat( captureEvent( eventBus, ActivityProvider.class ) ).hasValue( activity );
    verify( logger ).info( INFO_ACTIVITY_RATE, activity.getStatus() );
  }

  private static void stubMotionSensorAsEngaged( MotionSensor motionSensor ) {
    when( motionSensor.isEngaged() ).thenReturn( true );
  }

  private static void stubMotionSensorAsReleased( MotionSensor motionSensor ) {
    when( motionSensor.isEngaged() ).thenReturn( false );
  }

  private int captureMotionActivations( int times ) {
    for( int i = 0; i < times; i++ ) {
      activity.captureMotionActivations();
    }
    return times;
  }
}