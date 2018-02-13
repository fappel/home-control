package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.scene.HomeScope.LIVING_ROOM;

import java.util.Optional;

import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

public class DeskScene implements Scene {

  private final AdjustBrightnessOperation brightnessOperation;
  private final DeskScenePreference preference;
  private final LampControl lampControl;

  public DeskScene(
    LampControl lampControl, AdjustBrightnessOperation brightnessOperation, DeskScenePreference preference )
  {
    this.brightnessOperation = brightnessOperation;
    this.lampControl = lampControl;
    this.preference = preference;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public Optional<Scope> getScope() {
    return Optional.of( LIVING_ROOM );
  }

  @Override
  public void prepare() {
    lampControl.switchOnLamps( DeskUplight );
    lampControl.switchOffLamps( ChimneyUplight, FanLight1, FanLight2, WindowUplight );
    brightnessOperation.adjustLampMiniumBrightness( DeskUplight, preference.getLampMinimumBrightness() );
  }
}