package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.WORK_AREA;
import static com.codeaffine.home.control.status.type.Percent.P_000;
import static com.codeaffine.home.control.status.util.AnalysisComparator.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Optional;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.type.Percent;

public class Analysis {

  private static final SectionDefinition DUMMY_SECTION = WORK_AREA;

  private final MotionStatusCalculator motionStatusCalculator;
  private final ActivationSupplier activationProvider;
  private final ActivitySupplier activityProvider;

  Analysis(
    MotionStatusCalculator motionStatusCalculator,
    ActivationSupplier activationProvider,
    ActivitySupplier activityProvider )
  {
    verifyNotNull( motionStatusCalculator, "motionStatusCalculator" );
    verifyNotNull( activationProvider, "activationProvider" );
    verifyNotNull( activityProvider, "activityProvider" );

    this.motionStatusCalculator = motionStatusCalculator;
    this.activationProvider = activationProvider;
    this.activityProvider = activityProvider;
  }

  public Percent getOverallActivity() {
    return activityProvider.getStatus().getOverallActivity();
  }

  public ActivityStatus getOverallActivityStatus() {
    return ActivityStatus.valueOf( getOverallActivity() );
  }

  public boolean isOverallActivityStatusSameAs( ActivityStatus activityStatus ) {
    return isEqualTo( DUMMY_SECTION, definition -> getOverallActivityStatus(), activityStatus );
  }

  public boolean isOverallActivityStatusAtLeast( ActivityStatus activityStatus ) {
    return isAtLeast( DUMMY_SECTION, definition -> getOverallActivityStatus(), activityStatus );
  }

  public boolean isOverallActivityStatusAtMost( ActivityStatus activityStatus ) {
    return isAtMost( DUMMY_SECTION, definition -> getOverallActivityStatus(), activityStatus );
  }

  public Percent getActivity( SectionDefinition sectionDefinition ) {
    return activityProvider.getStatus().getSectionActivity( sectionDefinition ).orElse( P_000 );
  }

  public ActivityStatus getActivityStatus( SectionDefinition sectionDefinition ) {
    return ActivityStatus.valueOf( getActivity( sectionDefinition ) );
  }

  public boolean isActivityStatusSameAs( SectionDefinition sectionDefinition, ActivityStatus activityStatus ) {
    return isEqualTo( sectionDefinition, definition -> getActivityStatus( definition ), activityStatus );
  }

  public boolean isActivityStatusAtLeast( SectionDefinition sectionDefinition, ActivityStatus activityStatus ) {
    return isAtLeast( sectionDefinition, definition -> getActivityStatus( definition ), activityStatus );
  }

  public boolean isActivityStatusAtMost( SectionDefinition sectionDefinition, ActivityStatus activityStatus ) {
    return isAtMost( sectionDefinition, definition -> getActivityStatus( definition ), activityStatus );
  }

  public Percent getAllocation( SectionDefinition sectionDefinition ) {
    return activityProvider.getStatus().getSectionAllocation( sectionDefinition ).orElse( P_000 );
  }

  public AllocationStatus getAllocationStatus( SectionDefinition sectionDefinition ) {
    return AllocationStatus.valueOf( getAllocation( sectionDefinition ) );
  }

  public boolean isAllocationStatusSameAs( SectionDefinition sectionDefinition, AllocationStatus allocationStatus ) {
    return isEqualTo( sectionDefinition, definition -> getAllocationStatus( definition ), allocationStatus );
  }

  public boolean isAllocationStatusAtLeast( SectionDefinition sectionDefinition, AllocationStatus allocationStatus ) {
    return isAtLeast( sectionDefinition, definition -> getAllocationStatus( definition ), allocationStatus );
  }

  public boolean isAllocationStatusAtMost( SectionDefinition sectionDefinition, AllocationStatus allocationStatus ) {
    return isAtMost( sectionDefinition, definition -> getAllocationStatus( definition ), allocationStatus );
  }

  public MotionStatus getMotionStatus( SectionDefinition sectionDefinition ) {
    return motionStatusCalculator.getMotionStatus( sectionDefinition );
  }

  public boolean isMotionStatusSameAs( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    return isEqualTo( sectionDefinition, definition -> getMotionStatus( definition ), motionStatus );
  }

  public boolean isMotionStatusAtLeast( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    return isAtLeast( sectionDefinition, definition -> getMotionStatus( definition ), motionStatus );
  }

  public boolean isMotionStatusAtMost( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    return isAtMost( sectionDefinition, definition -> getMotionStatus( definition ), motionStatus );
  }

  public boolean isZoneActivated( SectionDefinition sectionDefinition ) {
    return activationProvider.getStatus().isZoneActivated( sectionDefinition );
  }

  public boolean isAdjacentZoneActivated( SectionDefinition sectionDefinition ) {
    Optional<Zone> zone = activationProvider.getStatus().getZone( sectionDefinition );
    if( zone.isPresent() ) {
      return zone.get().isAdjacentActivated();
    }
    return false;
  }
}