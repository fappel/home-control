package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.util.MotionStatus.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.type.Percent;

public class MotionStatusCalculator {

  static final int MEAN_DELTA_THRESHOLD = 10;

  private final ActivationSupplier activationProvider;
  private final ActivitySupplier activityProvider;
  private final ActivityMath activityMath;

  MotionStatusCalculator( ActivationSupplier activationProvider, ActivitySupplier activityProvider ) {
    verifyNotNull( activationProvider, "activationProvider" );
    verifyNotNull( activityProvider, "activityProvider" );

    this.activityMath = new ActivityMath( activityProvider, activationProvider );
    this.activationProvider = activationProvider;
    this.activityProvider = activityProvider;
  }

  public MotionStatus getMotionStatus( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    MotionStatus result = EVEN;
    if( activationProvider.getStatus().isZoneActivated( sectionDefinition ) ) {
      Percent maximum = activityMath.calculateMaximumOfPathAllocationFor( sectionDefinition ).get();
      Percent geometric = activityMath.calculateGeometricMeanOfPathAllocationFor( sectionDefinition ).get();
      Percent arithmetic = activityMath.calculateArithmeticMeanOfPathAllocationFor( sectionDefinition ).get();
      int delta = arithmetic.intValue() - geometric.intValue();
      Percent sectionAllocation = activityProvider.getStatus().getSectionAllocation( sectionDefinition ).get();
      if( sectionAllocation.compareTo( maximum ) == 0 && delta > MEAN_DELTA_THRESHOLD ) {
        result = FOCUSSED;
      }
    }
    return result;
  }
}