package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.home.control.application.util.Analysis.ActivityStatus.*;
import static com.codeaffine.home.control.application.util.Analysis.AllocationStatus.*;
import static com.codeaffine.home.control.application.util.Analysis.MotionStatus.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Optional;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.item.StringItem;

public class Analysis {

  private final ActivationProvider activationProvider;
  private final ActivityProvider activityProvider;
  private final StringItem overallActivity;
  private final ActivityMath activityMath;
  private final StringItem dressingArea;
  private final StringItem cookingArea;
  private final StringItem diningArea;
  private final StringItem livingArea;
  private final StringItem bathroom;
  private final StringItem workArea;
  private final StringItem hall;
  private final StringItem bed;

  public enum ActivityStatus {
    IDLE, QUIET, AROUSED, LIVELY, BRISK, BUSY, RUSH
  }

  public enum AllocationStatus {
    UNUSED, RARE, OCCASIONAL, FREQUENT, SUBSTANTIAL, CONTINUAL, PERMANENT
  }

  public enum MotionStatus {
    EVEN, FOCUSSED
  }

  Analysis( @ByName( "S_OVERALL_ACTIVITY" ) StringItem overallActivity,
            @ByName( "S_BED" ) StringItem bed,
            @ByName( "S_DRESSING_AREA" ) StringItem dressingArea,
            @ByName( "S_LIVING_AREA" ) StringItem livingArea,
            @ByName( "S_WORK_AREA" ) StringItem workArea,
            @ByName( "S_HALL" ) StringItem hall,
            @ByName( "S_COOKING_AREA" ) StringItem cookingArea,
            @ByName( "S_DINING_AREA" ) StringItem diningArea,
            @ByName( "S_BATH_ROOM" ) StringItem bathroom,
            ActivationProvider activationProvider,
            ActivityProvider activityProvider )
  {
    this.activityMath = new ActivityMath( activityProvider, activationProvider );
    this.activationProvider = activationProvider;
    this.overallActivity = overallActivity;
    this.activityProvider = activityProvider;
    this.dressingArea = dressingArea;
    this.cookingArea = cookingArea;
    this.diningArea = diningArea;
    this.livingArea = livingArea;
    this.bathroom = bathroom;
    this.workArea = workArea;
    this.hall = hall;
    this.bed = bed;
  }

  @Schedule( period = 2L )
  void update() {
    overallActivity.updateStatus( getOverallActivityStatus().toString() + " (" + getOverallActivity() + ")" );
    bed.updateStatus( getAreaStatus( BED ) );
    dressingArea.updateStatus( getAreaStatus( DRESSING_AREA ) );
    livingArea.updateStatus( getAreaStatus( LIVING_AREA ) );
    workArea.updateStatus( getAreaStatus( WORK_AREA ) );
    hall.updateStatus( getAreaStatus( HALL ) );
    cookingArea.updateStatus( getAreaStatus( COOKING_AREA ) );
    diningArea.updateStatus( getAreaStatus( DINING_AREA ) );
    bathroom.updateStatus( getAreaStatus( BATH_ROOM ) );
  }

  public Percent getOverallActivity() {
    return activityProvider.getStatus().getOverallActivity();
  }

  public Percent getActivity( SectionDefinition sectionDefinition ) {
    return activityProvider.getStatus().getSectionActivity( sectionDefinition ).orElse( P_000 );
  }

  public Percent getAllocation( SectionDefinition sectionDefinition ) {
    return activityProvider.getStatus().getSectionAllocation( sectionDefinition ).orElse( P_000 );
  }

  public ActivityStatus getOverallActivityStatus() {
    return getActivityStatus( getOverallActivity() );
  }

  public boolean isOverallActivitySameAs( ActivityStatus activityStatus ) {
    verifyNotNull( activityStatus, "activityStatus" );

    return getOverallActivityStatus().compareTo( activityStatus ) == 0;
  }

  public boolean isOverallActivityAtLeast( ActivityStatus activityStatus ) {
    verifyNotNull( activityStatus, "activityStatus" );

    return getOverallActivityStatus().compareTo( activityStatus ) >= 0;
  }

  public boolean isOverallActivityAtMost( ActivityStatus activityStatus ) {
    verifyNotNull( activityStatus, "activityStatus" );

    return getOverallActivityStatus().compareTo( activityStatus ) <= 0;
  }

  private ActivityStatus getActivityStatus( SectionDefinition sectionDefinition ) {
    Optional<Percent> sectionActivity = activityProvider.getStatus().getSectionActivity( sectionDefinition );
    return getActivityStatus( sectionActivity.orElse( P_000 ) );
  }

  public boolean isZoneActivitySameAs( SectionDefinition sectionDefinition, ActivityStatus activityStatus ) {
    verifyNotNull( activityStatus, "activityStatus" );

    return getActivityStatus( sectionDefinition ).compareTo( activityStatus ) == 0;
  }

  public boolean isZoneActivityAtLeast( SectionDefinition sectionDefinition, ActivityStatus activityStatus ) {
    verifyNotNull( activityStatus, "activityStatus" );

    return getActivityStatus( sectionDefinition ).compareTo( activityStatus ) >= 0;
  }

  public boolean isZoneActivityAtMost( SectionDefinition sectionDefinition, ActivityStatus activityStatus ) {
    verifyNotNull( activityStatus, "activityStatus" );

    return getActivityStatus( sectionDefinition ).compareTo( activityStatus ) <= 0;
  }

  public AllocationStatus getAllocationStatus( SectionDefinition sectionDefinition ) {
    Optional<Percent> sectionAllocation = activityProvider.getStatus().getSectionAllocation( sectionDefinition );
    if( sectionAllocation.isPresent() && sectionAllocation.get().compareTo( P_079 ) > 0 ) {
      return PERMANENT;
    }
    if( sectionAllocation.isPresent() && sectionAllocation.get().compareTo( P_049 ) > 0 ) {
      return CONTINUAL;
    }
    if( sectionAllocation.isPresent() && sectionAllocation.get().compareTo( P_029 ) > 0 ) {
      return SUBSTANTIAL;
    }
    if( sectionAllocation.isPresent() && sectionAllocation.get().compareTo( P_019 ) > 0 ) {
      return FREQUENT;
    }
    if( sectionAllocation.isPresent() && sectionAllocation.get().compareTo( P_009 ) > 0 ) {
      return OCCASIONAL;
    }
    if( sectionAllocation.isPresent() && sectionAllocation.get().compareTo( P_000 ) != 0 ) {
      return RARE;
    }
    return UNUSED;
  }

  public boolean isZoneAllocationSameAs( SectionDefinition sectionDefinition, AllocationStatus allocationStatus ) {
    verifyNotNull( allocationStatus, "allocationStatus" );

    return getAllocationStatus( sectionDefinition ).compareTo( allocationStatus ) == 0;
  }

  public boolean isZoneAllocationAtLeast( SectionDefinition sectionDefinition, AllocationStatus allocationStatus ) {
    verifyNotNull( allocationStatus, "allocationStatus" );

    return getAllocationStatus( sectionDefinition ).compareTo( allocationStatus ) >= 0;
  }

  public boolean isZoneAllocationAtMost( SectionDefinition sectionDefinition, AllocationStatus allocationStatus ) {
    verifyNotNull( allocationStatus, "allocationStatus" );

    return getAllocationStatus( sectionDefinition ).compareTo( allocationStatus ) <= 0;
  }

  public MotionStatus getMotionStatus( SectionDefinition sectionDefinition ) {
    MotionStatus result = EVEN;
    if( activationProvider.getStatus().isZoneActivated( sectionDefinition ) ) {
      Percent maximum = activityMath.calculateMaximumOfPathAllocationFor( sectionDefinition ).get();
      Percent geometric = activityMath.calculateGeometricMeanOfPathAllocationFor( sectionDefinition ).get();
      Percent arithmetic = activityMath.calculateArithmeticMeanOfPathAllocationFor( sectionDefinition ).get();
      int delta = arithmetic.intValue() - geometric.intValue();
      Percent sectionAllocation = activityProvider.getStatus().getSectionAllocation( sectionDefinition ).get();
      if( sectionAllocation.compareTo( maximum ) == 0 && delta > 10 ) {
        result = FOCUSSED;
      }
    }
    return result;
  }

  public boolean isMotionStatusSameAs( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    verifyNotNull( motionStatus, "motionStatus" );

    return getMotionStatus( sectionDefinition ).compareTo( motionStatus ) == 0;
  }

  public boolean isMotionStatusAtLeast( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    verifyNotNull( motionStatus, "motionStatus" );

    return getMotionStatus( sectionDefinition ).compareTo( motionStatus ) >= 0;
  }

  public boolean isMotionStatusAtMost( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    verifyNotNull( motionStatus, "motionStatus" );

    return getMotionStatus( sectionDefinition ).compareTo( motionStatus ) <= 0;
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

  private static ActivityStatus getActivityStatus( Percent activity ) {
    if( activity.compareTo( P_079 ) > 0 ) {
      return RUSH;
    }
    if( activity.compareTo( P_049 ) > 0 ) {
      return BUSY;
    }
    if( activity.compareTo( P_029 ) > 0 ) {
      return BRISK;
    }
    if( activity.compareTo( P_019 ) > 0 ) {
      return LIVELY;
    }
    if( activity.compareTo( P_009 ) > 0 ) {
      return AROUSED;
    }
    if( activity.compareTo( P_000 ) != 0 ) {
      return QUIET;
    }
    return IDLE;
  }

  private String getAreaStatus( SectionDefinition sectionDefinition ) {
    return   getActivityStatus( sectionDefinition ).toString()
           + ", "
           + getAllocationStatus( sectionDefinition ).toString()
           + ", "
           + getMotionStatus( sectionDefinition ).toString()
           + " ("
           + getActivity( sectionDefinition ).toString()
           + ", "
           + getAllocation( sectionDefinition ).toString()
           + ")";
  }
}