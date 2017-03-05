package com.codeaffine.home.control.application.sence;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;

public class NightScene implements Scene {

  private static final Collection<LampDefinition> EVENING_LAMPS
    = asList( SinkUplight,
              ChimneyUplight, WindowUplight,
              BedStand,
              BathRoomCeiling,
              HallCeiling );

  private final LampSwitchOperation lampSwitchOperation;

  public NightScene( LampSwitchOperation lampSwitchOperation ) {
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public void activate() {
    lampSwitchOperation.setLampFilter( lamp -> EVENING_LAMPS.contains( lamp.getDefinition() ) );
  }
}