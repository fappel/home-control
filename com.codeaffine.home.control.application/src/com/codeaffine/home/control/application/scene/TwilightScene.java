package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSelectionStrategy.ALL;
import static com.codeaffine.home.control.application.type.Percent.*;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.status.Scene;

public class TwilightScene implements Scene {

  private static final Collection<LampDefinition> TWILIGHT_LAMPS
    = asList( SinkUplight,
              KitchenCeiling,
              ChimneyUplight, WindowUplight,
              BedStand, BedRoomCeiling,
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
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    if( activity.getStatus().getOverallActivity().compareTo( P_000 ) < 0 ) {
      lampSwitchOperation.setLampSelectionStrategy( ALL );
    }
    lampSwitchOperation.setLampFilter( lamp -> TWILIGHT_LAMPS.contains( lamp.getDefinition() ) );
    adjustBrightnessOperation.setActivityThreshold( P_020 );
    adjustBrightnessOperation.setBrightnessMinimumAboveThreshold( P_020 );
  }
}