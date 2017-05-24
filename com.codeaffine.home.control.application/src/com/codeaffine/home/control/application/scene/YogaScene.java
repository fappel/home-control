package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;

import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.util.Analysis;

public class YogaScene extends LivingRoomSceneBase {

  private final LampControl lampControl;

  public YogaScene( LampControl lampControl, ComputerStatusSupplier computerStatusSupplier, Analysis analysis ) {
    super( computerStatusSupplier, analysis );
    this.lampControl = lampControl;
  }

  @Override
  public void prepare() {
    setTimeouts();
    switchLamps();
  }

  private void setTimeouts() {
    livingAreaTimeout.setIf( isWorkAreaHot() || isLivingAreaHot() );
  }

  private void switchLamps() {
    livingAreaTimeout.executeIfNotExpired( () -> {
      lampControl.switchOnLamps( ChimneyUplight, WindowUplight );
      lampControl.switchOffLamps( FanLight1, FanLight2, DeskUplight );
    } );
  }
}