package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampTimeoutModus.ON;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.SunLightStatus.TWILIGHT;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.function.Supplier;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampTimeoutModus;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.util.Analysis;

public class DayScene implements Scene {

  private static final Collection<LampDefinition> DAY_LAMPS
    = asList( KitchenCeiling,
              FanLight1, FanLight2,
              BedRoomCeiling,
              BathRoomCeiling,
              HallCeiling );

  private final LightThresholdUtil lightThresholdUtil;
  private final DayScenePreference preference;
  private final LampControl lampControl;
  private final Analysis analysis;

  DayScene(
    LampControl lampControl, Analysis analysis, LightThresholdUtil lightThresholdUtil, DayScenePreference preference )
  {
    this.lightThresholdUtil = lightThresholdUtil;
    this.lampControl = lampControl;
    this.analysis = analysis;
    this.preference = preference;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    lampControl.setLampFilter( lamp -> DAY_LAMPS.contains( lamp.getDefinition() ) );
    if( analysis.isSunLightStatusAtMost( TWILIGHT ) ) {
      configureLampTimeouts( () -> preference.getLampTimeoutModusNight() );
    } else {
      configureLampTimeouts( () -> preference.getLampTimeoutModusDay() );
    }
    lampControl.switchOffLamps( lightThresholdUtil.collectLampsOfZonesWithEnoughDayLight() );
  }

  private void configureLampTimeouts( Supplier<LampTimeoutModus> modusSupplier ) {
    lampControl.setLampTimeoutModus( modusSupplier.get() );
    if( modusSupplier.get() == ON ) {
      configureTimeoutRelatedSections();
    }
  }

  private void configureTimeoutRelatedSections() {
    lampControl.addGroupOfTimeoutRelatedSections( LIVING_AREA, WORK_AREA );
    lampControl.addGroupOfTimeoutRelatedSections( BED, BED_SIDE, DRESSING_AREA );
    lampControl.addGroupOfTimeoutRelatedSections( DINING_AREA, COOKING_AREA );
  }
}