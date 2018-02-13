package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.scene.HomeScope.KITCHEN;

import java.util.Optional;

import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

public class CookingScene implements Scene {

  private final AdjustBrightnessOperation brightnessOperation;
  private final CookingScenePreference preference;
  private final LampControl lampControl;

  public CookingScene(
    LampControl lampControl, AdjustBrightnessOperation brightnessOperation, CookingScenePreference preference )
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
    return Optional.of( KITCHEN );
  }

  @Override
  public void prepare() {
    lampControl.switchOnLamps( KitchenCeiling, SinkUplight );
    brightnessOperation.adjustLampMiniumBrightness(KitchenCeiling, preference.getLampMinimumBrightness());
    brightnessOperation.adjustLampMiniumBrightness(SinkUplight, preference.getLampMinimumBrightness());
  }
}