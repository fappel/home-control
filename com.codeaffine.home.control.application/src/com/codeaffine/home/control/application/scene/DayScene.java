package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.Scene;

public class DayScene implements Scene {

  private static final Collection<LampDefinition> DAY_LAMPS
    = asList( KitchenCeiling,
              FanLight1, FanLight2,
              BedRoomCeiling,
              BathRoomCeiling,
              HallCeiling );

  private final LampSwitchOperation lampSwitchOperation;

  public DayScene( LampSwitchOperation lampSwitchOperation ) {
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    lampSwitchOperation.setLampFilter( lamp -> DAY_LAMPS.contains( lamp.getDefinition() ) );
  }
}