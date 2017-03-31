package com.codeaffine.home.control.application.internal.activity;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.type.Percent;

abstract class RateCalculator {

  private final ActivationTracker activationTracker;
  private final Section section;

  RateCalculator( Section section, ActivationTracker activationTracker ) {
    this.activationTracker = activationTracker;
    this.section = section;
  }

  void setTimestampSupplier( Supplier<LocalDateTime> timestampSupplier ) {
    activationTracker.setTimestampSupplier( timestampSupplier );
  }

  Section getSection() {
    return section;
  }

  Percent calculate() {
    return activationTracker.calculateRate();
  }

  void captureActivations() {
    if( isActive() ) {
      activationTracker.captureActivation();
    } else {
      activationTracker.removeOldest();
    }
    activationTracker.removeExpired();
  }

  protected abstract boolean isActive();
}
