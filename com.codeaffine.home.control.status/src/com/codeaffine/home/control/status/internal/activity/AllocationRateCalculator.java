package com.codeaffine.home.control.status.internal.activity;

import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;

class AllocationRateCalculator extends RateCalculator {

  private final ActivationSupplier activationSupplier;

  AllocationRateCalculator( Section section, ActivationSupplier activationSupplier, ActivationTracker tracker ) {
    super( section, tracker );
    this.activationSupplier = activationSupplier;
  }

  @Override
  protected boolean isActive() {
    return activationSupplier.getStatus().isZoneActivated( getSection().getDefinition() );
  }
}
