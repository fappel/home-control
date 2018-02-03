package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.type.OnOff.ON;
import static com.codeaffine.home.control.status.util.AllocationStatus.*;

import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.application.util.TimeoutPreference;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.util.AllocationStatus;
import com.codeaffine.home.control.status.util.Analysis;

abstract class LivingRoomSceneBase implements Scene {

  static final AllocationStatus LIVING_AREA_ALLOCATION_THRESHOLD = CONTINUAL;
  static final AllocationStatus WORK_AREA_ALLOCATION_THRESHOLD = PERMANENT;

  protected final Timeout livingAreaTimeout;
  protected final Timeout workAreaTimeout;

  private final ComputerStatusSupplier computerStatusSupplier;
  private final Analysis analysis;

  LivingRoomSceneBase(
    ComputerStatusSupplier computerStatusSupplier, Analysis analysis, TimeoutPreference preference )
  {
    this.computerStatusSupplier = computerStatusSupplier;
    this.analysis = analysis;
    this.livingAreaTimeout = new Timeout( preference );
    this.workAreaTimeout = new Timeout( preference );
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  protected boolean isWorkAreaHot() {
    return    computerStatusSupplier.getStatus() == ON
           || analysis.isZoneActivated( WORK_AREA ) && !analysis.isAdjacentZoneActivated( WORK_AREA )
           || analysis.isAllocationStatusAtLeast( WORK_AREA, WORK_AREA_ALLOCATION_THRESHOLD );
  }

  protected boolean isLivingAreaHot() {
    return analysis.isZoneActivated( LIVING_AREA );
  }

  protected boolean isLivingAreaUsedALot() {
    return analysis.isAllocationStatusAtLeast( LIVING_AREA, LIVING_AREA_ALLOCATION_THRESHOLD );
  }
}