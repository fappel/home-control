package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.Messages.INFO_ACTIVITY_RATE;
import static com.codeaffine.home.control.application.type.Percent.P_000;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.Boolean.*;
import static java.lang.Math.min;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.time.LocalDateTime.now;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.control.StatusProviderCore;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

public class ActivityProviderImpl implements ActivityProvider {

  static final long CALCULATION_INTERVAL_DURATION = 10L; // Seconds
  static final long OBSERVATION_TIME_FRAME = 5L; // Minutes
  static final long SCAN_RATE = 2L; // Seconds
  static final BigDecimal MAX_ACTIVATIONS
    = new BigDecimal( OBSERVATION_TIME_FRAME * 60 )
        .divide( new BigDecimal( CALCULATION_INTERVAL_DURATION ), 2, ROUND_HALF_UP );

  private final StatusProviderCore<Percent> statusProviderCore;
  private final Queue<LocalDateTime> motionActivations;
  private final EntityRegistry entityRegistry;

  private Supplier<LocalDateTime> timestampSupplier;

  public ActivityProviderImpl( EntityRegistry entityRegistry, EventBus eventBus, Logger logger ) {
    verifyNotNull( entityRegistry, "entityRegistry" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.statusProviderCore = new StatusProviderCore<>( eventBus, P_000, this, logger );
    this.motionActivations = new LinkedList<>();
    this.entityRegistry = entityRegistry;
    this.timestampSupplier = () -> now();
  }

  @Override
  public Percent getStatus() {
    return statusProviderCore.getStatus();
  }

  @Schedule( period = SCAN_RATE )
  void calculateRate() {
    statusProviderCore.updateStatus( () -> doCalculateRate(), INFO_ACTIVITY_RATE );
  }

  @Schedule( period = CALCULATION_INTERVAL_DURATION )
  void captureMotionActivations() {
    if( hasActiveMotionSensors() ) {
      motionActivations.add( timestampSupplier.get() );
    }
    removeExpired();
  }

  void setTimestampSupplier( Supplier<LocalDateTime> timestampSupplier ) {
    this.timestampSupplier = timestampSupplier;
  }

  private Percent doCalculateRate() {
    return Percent.valueOf( new BigDecimal( min( motionActivations.size(), MAX_ACTIVATIONS.intValue() ) )
      .divide( MAX_ACTIVATIONS, 2, ROUND_HALF_UP )
      .multiply( new BigDecimal( 100 ) ).intValue() );
  }

  private boolean hasActiveMotionSensors() {
    return entityRegistry.findByDefinitionType( MotionSensorDefinition.class )
      .stream()
      .map( sensor -> valueOf( sensor.isEngaged() ) )
      .reduce( ( engaged1, engaged2 ) -> valueOf( logicalOr( engaged1.booleanValue(), engaged2.booleanValue() ) ) )
      .get()
      .booleanValue();
  }

  private void removeExpired() {
    while( hasExpiredTimestamps() ) {
      motionActivations.poll();
    }
  }

  private boolean hasExpiredTimestamps() {
    return    !motionActivations.isEmpty()
           && motionActivations.peek().plusMinutes( OBSERVATION_TIME_FRAME ).isBefore( now() );
  }
}