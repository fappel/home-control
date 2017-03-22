package com.codeaffine.home.control.application.internal.activity;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.application.type.Percent;

class SectionActivityProvider {

  private final ActivationTracker activationTracker;
  private final Section section;

  SectionActivityProvider( Section section, ActivationTracker activationTracker ) {
    this.activationTracker = activationTracker;
    this.section = section;
  }

  void setTimestampSupplier( Supplier<LocalDateTime> timestampSupplier ) {
    activationTracker.setTimestampSupplier( timestampSupplier );
  }

  Percent calculateRate() {
    return activationTracker.calculateRate();
  }

  void captureSensorActivations() {
    if( hasActiveSensor() ) {
      activationTracker.captureActivation();
    }
    activationTracker.removeExpired();
  }

  private boolean hasActiveSensor() {
    return section.getChildren( ActivationSensorDefinition.class ).stream().anyMatch( sensor -> sensor.isEngaged() );
  }
}