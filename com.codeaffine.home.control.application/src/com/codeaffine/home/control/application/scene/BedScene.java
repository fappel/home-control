package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.scene.HomeScope.BED_ROOM;

import java.util.Optional;

import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

public class BedScene implements Scene {

  private final AdjustBrightnessOperation brightnessOperation;
  private final BedScenePreference preference;
  private final LampControl lampControl;

  public BedScene(
    LampControl lampControl, AdjustBrightnessOperation brightnessOperation, BedScenePreference preference )
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
    return Optional.of( BED_ROOM );
  }

  @Override
  public void prepare() {
    lampControl.switchOnLamps( BedStand );
    lampControl.switchOffLamps( BedRoomCeiling );
    brightnessOperation.adjustLampMiniumBrightness( BedStand, preference.getLampMinimumBrightness() );
  }
}