package com.codeaffine.home.control.application.internal.activity;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.application.type.Percent;

class SectionActivityProvider {

  private final MotionActivationTracker motionActivationTracker;
  private final Section section;

  SectionActivityProvider( Section section, MotionActivationTracker motionActivationTracker ) {
    this.motionActivationTracker = motionActivationTracker;
    this.section = section;
  }

  void setTimestampSupplier( Supplier<LocalDateTime> timestampSupplier ) {
    motionActivationTracker.setTimestampSupplier( timestampSupplier );
  }

  Percent calculateRate() {
    return motionActivationTracker.calculateRate();
  }

  void captureMotionActivations() {
    if( hasActiveMotionSensor() ) {
      motionActivationTracker.captureMotionActivation();
    }
    motionActivationTracker.removeExpired();
  }

  private boolean hasActiveMotionSensor() {
    return section.getChildren( MotionSensorDefinition.class ).stream().anyMatch( sensor -> sensor.isEngaged() );
  }
}