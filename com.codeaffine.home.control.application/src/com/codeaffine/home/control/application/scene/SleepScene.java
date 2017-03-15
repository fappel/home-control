package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSelectionStrategy.NONE;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.Scene;

public class SleepScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;
  private final FollowUpTimer followUpTimer;

  public SleepScene( LampSwitchOperation lampSwitchOperation, FollowUpTimer followUpTimer ) {
    this.lampSwitchOperation = lampSwitchOperation;
    this.followUpTimer = followUpTimer;
  }

  @Override
  public void prepare() {
    followUpTimer.schedule( 20L, SECONDS, activateAwayMode() );
  }

  private Runnable activateAwayMode() {
    return () -> lampSwitchOperation.setLampSelectionStrategy( NONE );
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}