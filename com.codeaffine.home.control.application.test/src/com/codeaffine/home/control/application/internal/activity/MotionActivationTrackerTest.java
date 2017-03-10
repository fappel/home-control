package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.ActivityProviderImpl.*;
import static com.codeaffine.home.control.application.internal.activity.Util.calculateMaxActivations;
import static com.codeaffine.home.control.application.type.Percent.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.type.Percent;

public class MotionActivationTrackerTest {

  private static final BigDecimal MAX_ACTIVATIONS
    = calculateMaxActivations( OBSERVATION_TIME_FRAME, CALCULATION_INTERVAL );

  private MotionActivationTracker tracker;

  @Before
  public void setUp() {
    tracker = new MotionActivationTracker( OBSERVATION_TIME_FRAME, CALCULATION_INTERVAL );
  }

  @Test
  public void calculateInitialRate() {
    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void calculateRate() {
    captureMotionActivations( MAX_ACTIVATIONS.intValue() / 2 );

    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_050 );
  }

  @Test
  public void removeExpired() {
    tracker.setTimestampSupplier( () -> now().minusMinutes( ActivityProviderImpl.OBSERVATION_TIME_FRAME + 1 ) );
    captureMotionActivations( MAX_ACTIVATIONS.intValue() / 2 );

    tracker.removeExpired();
    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void removeExpiredIfAllUpToDate() {
    captureMotionActivations( MAX_ACTIVATIONS.intValue() / 2 );

    tracker.removeExpired();
    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_050 );
  }

  private void captureMotionActivations( int times ) {
    for( int i = 0; i < times; i++ ) {
      tracker.captureMotionActivation();
    }
  }
}