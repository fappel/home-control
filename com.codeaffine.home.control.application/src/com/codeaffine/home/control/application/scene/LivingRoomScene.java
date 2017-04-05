package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.OnOff.ON;
import static com.codeaffine.home.control.application.util.AllocationStatus.*;

import com.codeaffine.home.control.application.status.ComputerStatusProvider;
import com.codeaffine.home.control.application.util.AllocationStatus;
import com.codeaffine.home.control.application.util.Analysis;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;

class LivingRoomScene implements Scene {

  static final AllocationStatus LIVING_AREA_ALLOCATION_THRESHOLD = CONTINUAL;
  static final AllocationStatus WORK_AREA_ALLOCATION_THRESHOLD = PERMANENT;

  private final ComputerStatusProvider computerStatusProvider;
  private final Timeout livingAreaTimeout;
  private final Timeout workAreaTimeout;
  private final LampControl lampControl;
  private final Analysis analysis;

  LivingRoomScene( LampControl lampControl, ComputerStatusProvider computerStatusProvider, Analysis analysis ) {
    this.computerStatusProvider = computerStatusProvider;
    this.lampControl = lampControl;
    this.analysis = analysis;
    this.livingAreaTimeout = new Timeout();
    this.workAreaTimeout = new Timeout();
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    setTimeouts();
    switchLamps();
  }

  private void setTimeouts() {
    workAreaTimeout.setIf( isWorkAreaHot() );
    livingAreaTimeout.setIf( !isWorkAreaHot() || isWorkAreaHot() && isLivingAreaUsedALot() || isLivingAreaHot() );
  }

  private boolean isWorkAreaHot() {
    return    computerStatusProvider.getStatus() == ON
           || analysis.isZoneActivated( WORK_AREA ) && !analysis.isAdjacentZoneActivated( WORK_AREA )
           || analysis.isAllocationStatusAtLeast( WORK_AREA, WORK_AREA_ALLOCATION_THRESHOLD );
  }

  private boolean isLivingAreaHot() {
    return analysis.isZoneActivated( LIVING_AREA );
  }

  private boolean isLivingAreaUsedALot() {
    return analysis.isAllocationStatusAtLeast( LIVING_AREA, LIVING_AREA_ALLOCATION_THRESHOLD );
  }

  private void switchLamps() {
    workAreaTimeout.executeIfNotExpired( () -> lampControl.switchOnZoneLamps( WORK_AREA ) );
    livingAreaTimeout.executeIfNotExpired( () -> lampControl.setZoneLampsForFiltering( LIVING_AREA ) );
  }
}