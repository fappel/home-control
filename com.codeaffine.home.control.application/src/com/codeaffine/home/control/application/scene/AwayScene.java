package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.operation.LampSelectionStrategy.NONE;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.Scene;

public class AwayScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;
  private final FollowUpTimer followUpTimer;

  private boolean sleepMode;

  public AwayScene( LampSwitchOperation lampSwitchOperation, FollowUpTimer followUpTimer ) {
    this.lampSwitchOperation = lampSwitchOperation;
    this.followUpTimer = followUpTimer;
  }


  @Override
  public void prepare( Scene previous ) {
    if( previous != this ) {
      followUpTimer.schedule( 10L, SECONDS, activateSleepMode() );
      sleepMode = false;
    }
    if( sleepMode ) {
      switchAllLampsOff();
    }
  }

  private Runnable activateSleepMode() {
    return () -> switchAllLampsOff();
  }

  private void switchAllLampsOff() {
    sleepMode = true;
    lampSwitchOperation.setLampSelectionStrategy( NONE );
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}