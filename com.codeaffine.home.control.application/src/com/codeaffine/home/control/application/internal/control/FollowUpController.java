package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.Messages.ERROR_SCHEDULE_CALLED_OUTSIDE_OF_SCENE_ACTIVATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.control.StatusEvent;

class FollowUpController {

  private final Map<FollowUpKey, List<Runnable>> followUps;
  private final SystemExecutor executor;

  private boolean onFollowUpProcessing;
  private StatusEvent event;

  FollowUpController( SystemExecutor executor ) {
    this.followUps = new HashMap<>();
    this.executor = executor;
  }

  void process( StatusEvent event, Scene scene, Runnable preparations, Runnable actions ) {
    this.event = event;
    try {
      doProcessing( event, scene, preparations, actions );
    } finally {
      this.event = null;
    }
  }

  void schedule( Scene scene, long delay, TimeUnit unit, Runnable followUp, Consumer<StatusEvent> processCallback ) {
    verifySceneActivationBoundary();

    FollowUpKey key = new FollowUpKey( scene, event );
    if( !followUps.containsKey( key ) ) {
      followUps.put( key, new ArrayList<>() );
    }
    followUps.get( key ).add( followUp );
    executor.schedule( () -> executeFollowUp( processCallback, key ), delay, unit );
  }

  private void executeFollowUp( Consumer<StatusEvent> processCallback, FollowUpKey key ) {
    onFollowUpProcessing = true;
    try {
      processCallback.accept( key.getEvent() );
      followUps.remove( key );
    } finally {
      onFollowUpProcessing = false;
    }
  }

  private void verifySceneActivationBoundary() {
    if( event == null ) {
      throw new IllegalStateException( ERROR_SCHEDULE_CALLED_OUTSIDE_OF_SCENE_ACTIVATION );
    }
  }

  private void doProcessing( StatusEvent event, Scene scene, Runnable preparations, Runnable actions ) {
    if( onFollowUpProcessing ) {
      doFollowUpProcessing( event, scene, actions );
    } else {
      preparations.run();
      actions.run();
    }
  }

  private void doFollowUpProcessing( StatusEvent event, Scene scene, Runnable actions ) {
    FollowUpKey key = new FollowUpKey( scene, event );
    if( followUps.containsKey( key ) ) {
      followUps.get( key ).forEach( followUpHandler -> followUpHandler.run() );
      actions.run();
    }
  }
}