package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.Util.calculateMaxActivations;
import static java.lang.Math.min;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.time.LocalDateTime.now;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import com.codeaffine.home.control.application.type.Percent;

class ActivationTracker {

  private final Queue<LocalDateTime> activations;
  private final long observationTimeFrame;
  private final BigDecimal maxActivations;

  private Supplier<LocalDateTime> timestampSupplier;

  public ActivationTracker( long observationTimeFrame, long calculationIntervalDuration ) {
    this.maxActivations = calculateMaxActivations( observationTimeFrame, calculationIntervalDuration );
    this.observationTimeFrame = observationTimeFrame;
    this.activations = new LinkedList<>();
    this.timestampSupplier = () -> now();
  }

  void setTimestampSupplier( Supplier<LocalDateTime> timestampSupplier ) {
    this.timestampSupplier = timestampSupplier;
  }

  Percent calculateRate() {
    return Percent.valueOf( new BigDecimal( min( activations.size(), maxActivations.intValue() ) )
      .divide( maxActivations, 2, ROUND_HALF_UP )
      .multiply( new BigDecimal( 100 ) ).intValue() );
  }

  void captureActivation() {
    activations.add( timestampSupplier.get() );
  }

  void removeExpired() {
    while( hasExpiredTimestamps() ) {
      activations.poll();
    }
  }

  void removeOldest() {
    activations.poll();
  }

  private boolean hasExpiredTimestamps() {
    return    !activations.isEmpty()
           && activations.peek().plusMinutes( observationTimeFrame ).isBefore( now() );
  }
}