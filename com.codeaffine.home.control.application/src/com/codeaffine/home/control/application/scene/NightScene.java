package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.Scene;

public class NightScene implements Scene {

  private static final Collection<LampDefinition> EVENING_LAMPS
    = asList( SinkUplight,
              ChimneyUplight, WindowUplight,
              BedStand, BedRoomCeiling,
              BathRoomCeiling,
              HallCeiling );

  private final LampSwitchOperation lampSwitchOperation;

  public NightScene( LampSwitchOperation lampSwitchOperation ) {
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public void prepare() {
    lampSwitchOperation.setLampFilter( lamp -> EVENING_LAMPS.contains( lamp.getDefinition() ) );
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}