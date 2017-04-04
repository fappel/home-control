package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.util.MotionStatus.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;

public class MotionStatusCalculator {

  static final int MEAN_DELTA_THRESHOLD = 10;

  private final ActivationProvider activationProvider;
  private final ActivityProvider activityProvider;
  private final ActivityMath activityMath;

  MotionStatusCalculator( ActivationProvider activationProvider, ActivityProvider activityProvider ) {
    verifyNotNull( activationProvider, "activationProvider" );
    verifyNotNull( activityProvider, "activityProvider" );

    this.activityMath = new ActivityMath( activityProvider, activationProvider );
    this.activationProvider = activationProvider;
    this.activityProvider = activityProvider;
  }

  MotionStatus getMotionStatus( SectionDefinition sectionDefinition ) {
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