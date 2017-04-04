package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.OnOff.ON;
import static com.codeaffine.home.control.application.util.AllocationStatus.*;

import com.codeaffine.home.control.application.status.ComputerStatusProvider;
import com.codeaffine.home.control.application.util.Analysis;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;

public class LivingRoomScene implements Scene {

  private final ComputerStatusProvider computerStatusProvider;
  private final Timeout livingAreaTimeout;
  private final Timeout workAreaTimeout;
  private final LampControl lampControl;
  private final Analysis analysis;

  public LivingRoomScene( LampControl lampControl, ComputerStatusProvider computerStatusProvider, Analysis analysis ) {
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
    workAreaTimeout.setIf( isWorkAreaHot() );
    livingAreaTimeout.setIf( !isWorkAreaHot() || isWorkAreaHot() && isLivingAreaUsedALot() || isLivingAreaHot() );
    workAreaTimeout.executeIfNotExpired( () -> lampControl.switchOnZoneLamps( WORK_AREA ) );
    livingAreaTimeout.executeIfNotExpired( () -> lampControl.setZoneLampsForFiltering( LIVING_AREA ) );
  }

  private boolean isWorkAreaHot() {
    return    computerStatusProvider.getStatus() == ON
           || analysis.isZoneActivated( WORK_AREA ) && !analysis.isAdjacentZoneActivated( WORK_AREA )
           || analysis.isAllocationStatusAtLeast( WORK_AREA, PERMANENT );
  }

  private boolean isLivingAreaHot() {
    return analysis.isZoneActivated( LIVING_AREA );
  }

  private boolean isLivingAreaUsedALot() {
    return analysis.isAllocationStatusAtLeast( LIVING_AREA, CONTINUAL );
  }
}