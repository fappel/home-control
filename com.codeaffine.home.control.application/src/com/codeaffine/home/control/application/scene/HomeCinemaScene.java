package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.LIVING_AREA;

import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.status.Scene;

public class HomeCinemaScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;
  private final ActivationProvider activationProvider;

  public HomeCinemaScene( LampSwitchOperation lampSwitchOperation, ActivationProvider activationProvider ) {
    this.lampSwitchOperation = lampSwitchOperation;
    this.activationProvider = activationProvider;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    if( isLivingAreaActive() ) {
      lampSwitchOperation.setLampsToSwitchOn( ChimneyUplight, WindowUplight );
      lampSwitchOperation.setLampsToSwitchOff( DeskUplight, FanLight1, FanLight2 );
    }
  }

  private boolean isLivingAreaActive() {
    return activationProvider.getStatus().getZone( LIVING_AREA ).isPresent();
  }
}