package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.LIVING_AREA;

import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;

public class HomeCinemaScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;
  private final ActivationSupplier activationSupplier;

  public HomeCinemaScene( LampSwitchOperation lampSwitchOperation, ActivationSupplier activationSupplier ) {
    this.lampSwitchOperation = lampSwitchOperation;
    this.activationSupplier = activationSupplier;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    if( isLivingAreaActive() ) {
      lampSwitchOperation.addLampsToSwitchOn( ChimneyUplight, WindowUplight );
      lampSwitchOperation.addLampsToSwitchOff( DeskUplight, FanLight1, FanLight2 );
    }
  }

  private boolean isLivingAreaActive() {
    return activationSupplier.getStatus().getZone( LIVING_AREA ).isPresent();
  }
}