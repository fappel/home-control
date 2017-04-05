package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.type.OnOff.ON;
import static com.codeaffine.home.control.status.util.AllocationStatus.*;

import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.util.AllocationStatus;
import com.codeaffine.home.control.status.util.Analysis;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;

class LivingRoomScene implements Scene {

  static final AllocationStatus LIVING_AREA_ALLOCATION_THRESHOLD = CONTINUAL;
  static final AllocationStatus WORK_AREA_ALLOCATION_THRESHOLD = PERMANENT;

  private final ComputerStatusSupplier computerStatusSupplier;
  private final Timeout livingAreaTimeout;
  private final Timeout workAreaTimeout;
  private final LampControl lampControl;
  private final Analysis analysis;

  LivingRoomScene( LampControl lampControl, ComputerStatusSupplier computerStatusSupplier, Analysis analysis ) {
    this.computerStatusSupplier = computerStatusSupplier;
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
    return    computerStatusSupplier.getStatus() == ON
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