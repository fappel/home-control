package com.codeaffine.home.control.application;

import static java.lang.Boolean.*;
import static java.lang.Math.min;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.time.LocalDateTime.now;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

public class Activity {

  private static final long CALCULATION_INTERVAL_DURATION = 10L; // Seconds
  private static final long OBSERVATION_TIME_FRAME = 5L; // Minutes
  private static final long SCAN_RATE = 2L; // Seconds
  private static final BigDecimal MAX_ACTIVATIONS
    = new BigDecimal( OBSERVATION_TIME_FRAME * 60 )
        .divide( new BigDecimal( CALCULATION_INTERVAL_DURATION ), 2, ROUND_HALF_UP );

  private final Queue<LocalDateTime> motionActivations;
  private final EntityRegistry entityRegistry;
  private final EventBus eventBus;
  private final Logger logger;

  private int activityRate;

  public Activity( EntityRegistry entityRegistry, EventBus eventBus, Logger logger ) {
    this.motionActivations = new LinkedList<>();
    this.entityRegistry = entityRegistry;
    this.eventBus = eventBus;
    this.logger = logger;
  }

  public int getActivityRate() {
    return activityRate;
  }

  @Schedule( period = SCAN_RATE )
  void calculateRate() {
    int oldActivityRate = activityRate;
    activityRate = doCalculateRate();
    if( oldActivityRate != activityRate ) {
      eventBus.post( new Event( this ) );
      logger.info( "activityRate: " + activityRate );
    }
  }

  @Schedule( period = CALCULATION_INTERVAL_DURATION )
  void updateMotionActivations() {
    if( hasActiveMotionSensors() ) {
      motionActivations.add( now() );
    }
    removeExpired();
  }

  private int doCalculateRate() {
    return new BigDecimal( min( motionActivations.size(), MAX_ACTIVATIONS.intValue() ) )
      .divide( MAX_ACTIVATIONS, 2, ROUND_HALF_UP )
      .multiply( new BigDecimal( 100 ) ).intValue();
  }

  private boolean hasActiveMotionSensors() {
    return entityRegistry.findByDefinitionType( MotionSensorDefinition.class )
      .stream()
      .map( sensor -> valueOf( sensor.isEngaged() ) )
      .reduce( ( engaged1, engaged2 ) -> valueOf( logicalAnd( engaged1.booleanValue(), engaged1.booleanValue() ) ) )
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