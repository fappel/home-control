package com.codeaffine.home.control.application.sence;

import static com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSwitchSelectionStrategy.NONE;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;

public class AwayScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;
  private final FollowUpTimer followUpTimer;

  public AwayScene( LampSwitchOperation lampSwitchOperation, FollowUpTimer followUpTimer ) {
    this.lampSwitchOperation = lampSwitchOperation;
    this.followUpTimer = followUpTimer;
  }

  @Override
  public void activate() {
    followUpTimer.schedule( 20L, SECONDS, activateAwayMode() );
  }

  private Runnable activateAwayMode() {
    return () -> lampSwitchOperation.setLampSwitchSelectionStrategy( NONE );
  }
}