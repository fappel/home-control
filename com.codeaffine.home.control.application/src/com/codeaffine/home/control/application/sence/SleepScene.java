package com.codeaffine.home.control.application.sence;

import static com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSelectionStrategy.NONE;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;

public class SleepScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;
  private final FollowUpTimer followUpTimer;

  public SleepScene( LampSwitchOperation lampSwitchOperation, FollowUpTimer followUpTimer ) {
    this.lampSwitchOperation = lampSwitchOperation;
    this.followUpTimer = followUpTimer;
  }

  @Override
  public void activate() {
    followUpTimer.schedule( 20L, SECONDS, activateAwayMode() );
  }

  private Runnable activateAwayMode() {
    return () -> lampSwitchOperation.setLampSelectionStrategy( NONE );
  }
}