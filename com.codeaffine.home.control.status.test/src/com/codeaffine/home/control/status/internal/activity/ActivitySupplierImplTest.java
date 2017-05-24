package com.codeaffine.home.control.status.internal.activity;

import static com.codeaffine.home.control.status.internal.activity.ActivitySupplierImpl.*;
import static com.codeaffine.home.control.status.internal.activity.Messages.INFO_ACTIVITY_RATE;
import static com.codeaffine.home.control.status.internal.activity.Util.calculateMaxActivations;
import static com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.createZone;
import static com.codeaffine.home.control.status.test.util.supplier.ActivityAssert.assertThat;
import static com.codeaffine.home.control.status.test.util.model.ModelRegistryHelper.*;
import static com.codeaffine.home.control.status.type.Percent.*;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static com.codeaffine.home.control.test.util.event.EventBusHelper.captureEvent;
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

import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.internal.activity.ActivitySupplierImpl;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.Activity;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.Activation.Zone;

public class ActivitySupplierImplTest {

  private static final BigDecimal MAX_ACTIVATIONS
    = calculateMaxActivations( OBSERVATION_TIME, CALCULATION_INTERVAL );

  private ActivationSupplier activationSupplier;
  private ActivationSensor bathRoomSensor;
  private ActivitySupplierImpl activitySupplier;
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
      = stubRegistry( asList( bed, bathroom, livingRoom ), asList( bedSensor, bathRoomSensor ) );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    activationSupplier = stubEmptyActivationSupplier();
    activitySupplier = new ActivitySupplierImpl( activationSupplier, registry, eventBus, logger );
  }

  @Test
  public void calculateRate() {
    int dryRun = captureActivations( MAX_ACTIVATIONS.intValue() / 2 );
    stubActivationSensorAsEngaged( bedSensor );
    stubActivationSupplier( BED );
    captureActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubActivationSensorAsReleased( bedSensor );
    stubActivationSensorAsEngaged( bathRoomSensor );
    stubActivationSupplier( BATH_ROOM );
    captureActivations( MAX_ACTIVATIONS.intValue() / 6 );

    activitySupplier.calculateRate();
    Activity actual = activitySupplier.getStatus();

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
    stubActivationSupplier( BED );
    captureActivations( MAX_ACTIVATIONS.intValue()  );

    activitySupplier.calculateRate();
    Activity actual = activitySupplier.getStatus();

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
    stubActivationSupplier( BED );
    captureActivations( MAX_ACTIVATIONS.intValue() + 1 );

    activitySupplier.calculateRate();
    Activity actual = activitySupplier.getStatus();

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
    activitySupplier.calculateRate();

    Activity actual = activitySupplier.getStatus();

    assertThat( actual )
      .hasOverallActivity( P_000 )
      .hasSectionActivity( BED, P_000 )
      .hasSectionActivity( BATH_ROOM, P_000 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_000 )
      .hasSectionAllocation( BATH_ROOM, P_000 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
    verify( logger, never() ).debug( anyString(), eq( actual ) );
  }

  @Test
  public void calculateRateIfNoActivationHasBeenCaptured() {
    int dryRun = captureActivations( MAX_ACTIVATIONS.intValue() / 2 );

    activitySupplier.calculateRate();
    Activity actual = activitySupplier.getStatus();

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
    verify( logger, never() ).debug( anyString(), eq( actual ) );
  }

  @Test
  public void calculateRateWithExpiredTimestamps() {
    int dryRun = captureActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubActivationSensorAsEngaged( bedSensor );
    stubActivationSupplier( BED );
    activitySupplier.setTimestampSupplier( () -> now().minusMinutes( ActivitySupplierImpl.OBSERVATION_TIME + 1 ) );
    captureActivations( MAX_ACTIVATIONS.intValue() / 3 );
    stubActivationSensorAsReleased( bedSensor );
    stubActivationSensorAsEngaged( bathRoomSensor );
    stubActivationSupplier( BATH_ROOM );
    activitySupplier.setTimestampSupplier( () -> now() );
    captureActivations( MAX_ACTIVATIONS.intValue() / 3 );

    activitySupplier.calculateRate();
    Activity actual = activitySupplier.getStatus();

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
  public void constructWithNullAsActivationSupplierArgument() {
    new ActivitySupplierImpl( null, mock( EntityRegistry.class ), eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRegistryArgument() {
    new ActivitySupplierImpl( activationSupplier, null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new ActivitySupplierImpl( activationSupplier, mock( EntityRegistry.class ), null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new ActivitySupplierImpl( activationSupplier, mock( EntityRegistry.class ), eventBus, null );
  }

  private void verifyEventNotification() {
    assertThat( captureEvent( eventBus, ActivitySupplier.class ) ).hasValue( activitySupplier );
    verify( logger ).debug( INFO_ACTIVITY_RATE, activitySupplier.getStatus() );
  }

  private static void stubActivationSensorAsEngaged( ActivationSensor sensor ) {
    when( sensor.isEngaged() ).thenReturn( true );
  }

  private static void stubActivationSensorAsReleased( ActivationSensor sensor ) {
    when( sensor.isEngaged() ).thenReturn( false );
  }

  private void stubActivationSupplier( SectionDefinition ... definitions ) {
    Set<Zone> zones = Stream.of( definitions ).map( def -> createZone( stubEntity( def ) ) ).collect( toSet() );
    when( activationSupplier.getStatus() ).thenReturn( new Activation( zones ) );
  }

  private static ActivationSupplier stubEmptyActivationSupplier() {
    ActivationSupplier result = mock( ActivationSupplier.class );
    when( result.getStatus() ).thenReturn( new Activation( emptySet() ) );
    return result;
  }

  private int captureActivations( int times ) {
    asList( new Object[ times ] ).forEach( nix -> activitySupplier.captureActivations() );
    return times;
  }
}