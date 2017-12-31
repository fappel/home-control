package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.GLOBAL;

import java.util.Optional;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

public class AllLightsOffScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;

  public AllLightsOffScene( LampSwitchOperation lampSwitchOperation ) {
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public Optional<Scope> getScope() {
    return Optional.of( GLOBAL );
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    lampSwitchOperation.addLampsToSwitchOff( LampDefinition.values() );
  }
}