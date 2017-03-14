package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;

import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.Scene;

public class WorkAreaScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;

  public WorkAreaScene( LampSwitchOperation lampSwitchOperation ) {
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public String getName() {
    return getClass().getName();
  }

  @Override
  public void activate() {
    lampSwitchOperation.setLampsToSwitchOff( FanLight1, FanLight2, WindowUplight );
    lampSwitchOperation.setLampsToSwitchOn( DeskUplight, ChimneyUplight );
  }
}