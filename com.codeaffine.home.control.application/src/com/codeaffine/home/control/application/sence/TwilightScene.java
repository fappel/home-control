package com.codeaffine.home.control.application.sence;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSwitchSelectionStrategy.ALL;
import static com.codeaffine.home.control.application.type.Percent.P_020;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.status.ActivityProvider;

public class TwilightScene implements Scene {

  private static final Collection<LampDefinition> NIGHT_LAMPS
    = asList( SinkUplight,
              ChimneyUplight, WindowUplight,
              BedStand,
              BathRoomCeiling,
              HallCeiling );

  private final AdjustBrightnessOperation adjustBrightnessOperation;
  private final LampSwitchOperation lampSwitchOperation;
  private final ActivityProvider activity;

  public TwilightScene( ActivityProvider activity,
                         LampSwitchOperation lampSwitchOperation,
                         AdjustBrightnessOperation adjustBrightnessOperation )
  {
    this.adjustBrightnessOperation = adjustBrightnessOperation;
    this.lampSwitchOperation = lampSwitchOperation;
    this.activity = activity;
  }

  @Override
  public void activate() {
    if( activity.getStatus().compareTo( P_020 ) < 0 ) {
      lampSwitchOperation.setLampSwitchSelectionStrategy( ALL );
    }
    lampSwitchOperation.setLampFilter( lamp -> NIGHT_LAMPS.contains( lamp.getDefinition() ) );
    adjustBrightnessOperation.setActivityThreshold( P_020 );
    adjustBrightnessOperation.setBrightnessMinimumAboveThreshold( P_020 );
  }
}