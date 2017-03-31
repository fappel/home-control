package com.codeaffine.home.control.application.internal.activity;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.status.ActivationProvider;

class AllocationRateCalculator extends RateCalculator {

  private final ActivationProvider activationProvider;

  AllocationRateCalculator( Section section, ActivationProvider activationProvider, ActivationTracker tracker ) {
    super( section, tracker );
    this.activationProvider = activationProvider;
  }

  @Override
  protected boolean isActive() {
    return activationProvider.getStatus().isZoneActivated( getSection().getDefinition() );
  }
}
