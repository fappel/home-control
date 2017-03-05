package com.codeaffine.home.control.application.sence;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;


public class DayScene implements Scene {

  private static final Collection<LampDefinition> DAY_LAMPS
    = asList( SinkUplight, KitchenCeiling,
              ChimneyUplight, WindowUplight, FanLight1, FanLight2,
              BedStand, BedRoomCeiling,
              BathRoomCeiling,
              HallCeiling );

  private final LampSwitchOperation lampSwitchOperation;

  public DayScene( LampSwitchOperation lampSwitchOperation ) {
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public void activate() {
    lampSwitchOperation.setLampFilter( lamp -> DAY_LAMPS.contains( lamp.getDefinition() ) );
  }
}
