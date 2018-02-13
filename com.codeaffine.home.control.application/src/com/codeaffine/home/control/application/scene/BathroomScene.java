package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.scene.HomeScope.BATH_ROOM;

import java.util.Optional;

import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

public class BathroomScene implements Scene {

  private final AdjustBrightnessOperation brightnessOperation;
  private final BathroomScenePreference preference;
  private final LampControl lampControl;

  public BathroomScene(
    LampControl lampControl, AdjustBrightnessOperation brightnessOperation, BathroomScenePreference preference )
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
    return Optional.of( BATH_ROOM );
  }

  @Override
  public void prepare() {
    lampControl.switchOnLamps( BathRoomCeiling );
    brightnessOperation.adjustLampMiniumBrightness( BathRoomCeiling, preference.getLampMinimumBrightness() );
  }
}