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

public class ActivationTrackerTest {

  private static final BigDecimal MAX_ACTIVATIONS
    = calculateMaxActivations( OBSERVATION_TIME, CALCULATION_INTERVAL );

  private ActivationTracker tracker;

  @Before
  public void setUp() {
    tracker = new ActivationTracker( OBSERVATION_TIME, CALCULATION_INTERVAL );
  }

  @Test
  public void calculateInitialRate() {
    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void calculateRate() {
    captureActivations( MAX_ACTIVATIONS.intValue() / 2 );

    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_050 );
  }

  @Test
  public void calculateRateWithMaxActivations() {
    captureActivations( MAX_ACTIVATIONS.intValue() );

    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_100 );
  }

  @Test
  public void removeExpired() {
    tracker.setTimestampSupplier( () -> now().minusMinutes( ActivityProviderImpl.OBSERVATION_TIME + 1 ) );
    captureActivations( MAX_ACTIVATIONS.intValue() / 2 );

    tracker.removeExpired();
    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void removeExpiredIfAllUpToDate() {
    captureActivations( MAX_ACTIVATIONS.intValue() / 2 );

    tracker.removeExpired();
    Percent actual = tracker.calculateRate();

    assertThat( actual ).isSameAs( P_050 );
  }

  @Test
  public void removeOldest() {
    captureActivations( MAX_ACTIVATIONS.intValue() );

    tracker.removeOldest();
    Percent actual = tracker.calculateRate();

    assertThat( actual.intValue() )
      .isGreaterThan( P_050.intValue() )
      .isNotSameAs( P_100.intValue() );
  }

  private void captureActivations( int times ) {
      for( int i = 0; i < times; i++ ) {
        tracker.captureActivation();
      }
    }
}