package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.Scene;

public class AllLightsOffScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;

  public AllLightsOffScene( LampSwitchOperation lampSwitchOperation ) {
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    lampSwitchOperation.setLampsToSwitchOff( LampDefinition.values() );
  }
}