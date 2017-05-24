package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.WORK_AREA;

import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.util.Analysis;

public class HomeCinemaScene extends LivingRoomSceneBase {

  private final LampControl lampControl;

  public HomeCinemaScene( LampControl lampControl, ComputerStatusSupplier computerStatusSupplier, Analysis analysis ) {
    super( computerStatusSupplier, analysis );
    this.lampControl = lampControl;
  }

  @Override
  public void prepare() {
    setTimeouts();
    switchLamps();
  }

  private void setTimeouts() {
    workAreaTimeout.setIf( isWorkAreaHot() );
    livingAreaTimeout.setIf( isWorkAreaHot() || isLivingAreaHot() );
  }

  private void switchLamps() {
    workAreaTimeout.executeIfNotExpired( () -> lampControl.switchOnZoneLamps( WORK_AREA ) );
    livingAreaTimeout.executeIfNotExpired( () -> {
      lampControl.switchOnLamps( ChimneyUplight, WindowUplight );
      lampControl.switchOffLamps( FanLight1, FanLight2 );
    } );
  }
}