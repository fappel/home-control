package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampTimeoutModus.ON;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.SunLightStatus.TWILIGHT;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
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
  private final LampControl lampControl;
  private final Analysis analysis;

  DayScene( LampControl lampControl, Analysis analysis, LightThresholdUtil lightThresholdUtil ) {
    this.lightThresholdUtil = lightThresholdUtil;
    this.lampControl = lampControl;
    this.analysis = analysis;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    lampControl.setLampFilter( lamp -> DAY_LAMPS.contains( lamp.getDefinition() ) );
    if( analysis.isSunLightStatusAtMost( TWILIGHT ) ) {
      lampControl.setLampTimeoutModus( ON );
      lampControl.addGroupOfTimeoutRelatedSections( LIVING_AREA, WORK_AREA );
      lampControl.addGroupOfTimeoutRelatedSections( BED, BED_SIDE, DRESSING_AREA );
      lampControl.addGroupOfTimeoutRelatedSections( DINING_AREA, COOKING_AREA );
    }
    lampControl.switchOffLamps( lightThresholdUtil.collectLampsOfZonesWithEnoughDayLight() );
  }
}