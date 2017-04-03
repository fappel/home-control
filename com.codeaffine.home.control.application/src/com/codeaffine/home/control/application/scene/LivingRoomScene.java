package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.OnOff.ON;
import static com.codeaffine.home.control.application.util.Analysis.AllocationStatus.*;

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
    if(    computerStatusProvider.getStatus() == ON
        || analysis.isZoneActivated( WORK_AREA ) && !analysis.isAdjacentZoneActivated( WORK_AREA )
        || analysis.isZoneAllocationAtLeast( WORK_AREA, PERMANENT ) )
    {
      workAreaTimeout.set();
      if( analysis.isZoneAllocationAtLeast( LIVING_AREA, CONTINUAL ) ) {
        livingAreaTimeout.set();
      }
    } else {
      livingAreaTimeout.set();
    }

    if( analysis.isZoneActivated( LIVING_AREA ) ) {
      livingAreaTimeout.set();
    }

    if( !workAreaTimeout.isExpired() ) {
      lampControl.switchOnZoneLamps( WORK_AREA );
    }
    if( !livingAreaTimeout.isExpired() ) {
      lampControl.provideZoneLampsForFiltering( LIVING_AREA );
    }
  }
}