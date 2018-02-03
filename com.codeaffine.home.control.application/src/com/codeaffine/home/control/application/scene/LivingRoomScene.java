package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;

import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.TimeoutPreference;
import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.util.Analysis;

class LivingRoomScene extends LivingRoomSceneBase {

  private final LampControl lampControl;

  LivingRoomScene(
    LampControl lampControl,
    ComputerStatusSupplier computerStatusSupplier,
    Analysis analysis,
    TimeoutPreference preference )
  {
    super( computerStatusSupplier, analysis, preference );
    this.lampControl = lampControl;
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

  private void switchLamps() {
    workAreaTimeout.executeIfNotExpired( () -> lampControl.switchOnZoneLamps( WORK_AREA ) );
    livingAreaTimeout.executeIfNotExpired( () -> lampControl.setZoneLampsForFiltering( LIVING_AREA ) );
  }
}