package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.Messages.INFO_ACTIVITY_RATE;
import static com.codeaffine.home.control.application.test.EventBusHelper.captureEvent;
import static com.codeaffine.home.control.application.type.Percent.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;

public class ActivityProviderImplTest {

  private MotionSensor motionSensor1;
  private MotionSensor motionSensor2;
  private ActivityProviderImpl activity;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    motionSensor1 = mock( MotionSensor.class );
    motionSensor2 = mock( MotionSensor.class );
    EntityRegistry entityRegistry = stubEntityRegistry( motionSensor1, motionSensor2 );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    activity = new ActivityProviderImpl( entityRegistry, eventBus, logger );
  }

  @Test
  public void calculateRate() {
    int dryRun = captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() / 2 );
    stubMotionSensorAsEngaged( motionSensor1 );
    captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() / 3 );
    stubMotionSensorAsReleased( motionSensor1 );
    stubMotionSensorAsEngaged( motionSensor2 );
    captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() / 6 );

    activity.calculateRate();
    Percent actual = activity.getStatus();

    assertThat( actual ).isSameAs( P_050 );
    assertThat( dryRun ).isGreaterThan( 0 );
    verifyEventNotification();
  }

  @Test
  public void calculateRateOnMaximumOfExpectedActivations() {
    stubMotionSensorAsEngaged( motionSensor1 );
    captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue()  );

    activity.calculateRate();
    Percent actual = activity.getStatus();

    assertThat( actual ).isSameAs( P_100 );
    verifyEventNotification();
  }

  @Test
  public void calculateRateOnExpectedActivationOverflow() {
    stubMotionSensorAsEngaged( motionSensor1 );
    captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() + 1 );

    activity.calculateRate();
    Percent actual = activity.getStatus();

    assertThat( actual ).isSameAs( P_100 );
    verifyEventNotification();
  }

  @Test
  public void calculateRateAfterCreation() {
    activity.calculateRate();
    Percent actual = activity.getStatus();

    assertThat( actual ).isSameAs( P_000 );
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
    verify( logger, never() ).info( anyString(), eq( activity.getStatus () ) );
  }

  @Test
  public void calculateRateIfNoMotionActivationHasBeenCaptured() {
    int dryRun = captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() / 2 );

    activity.calculateRate();
    Percent actual = activity.getStatus();

    assertThat( actual ).isSameAs( P_000 );
    assertThat( dryRun ).isGreaterThan( 0 );
    verify( eventBus, never() ).post( any( StatusEvent.class ) );
    verify( logger, never() ).info( anyString(), eq( activity.getStatus() ) );
  }

  @Test
  public void calculateRateWithExpiredTimestamps() {
    int dryRun = captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() / 3 );
    stubMotionSensorAsEngaged( motionSensor1 );
    activity.setTimestampSupplier( () -> now().minusMinutes( ActivityProviderImpl.OBSERVATION_TIME_FRAME + 1 ) );
    captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() / 3 );
    stubMotionSensorAsReleased( motionSensor1 );
    stubMotionSensorAsEngaged( motionSensor2 );
    activity.setTimestampSupplier( () -> now() );
    captureMotionActivations( ActivityProviderImpl.MAX_ACTIVATIONS.intValue() / 3 );

    activity.calculateRate();
    Percent actual = activity.getStatus();

    assertThat( actual ).isSameAs( P_033 );
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

  private static EntityRegistry stubEntityRegistry( MotionSensor ... motionSensors ) {
    EntityRegistry result = mock( EntityRegistry.class );
    when( result.findByDefinitionType( MotionSensorDefinition.class ) ).thenReturn( asList( motionSensors ) );
    return result;
  }
}