package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.Messages.ERROR_SCHEDULE_CALLED_OUTSIDE_OF_SCENE_ACTIVATION;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.status.StatusEvent;

class FollowUpController {

  private final Map<FollowUpKey, List<Runnable>> followUps;
  private final SystemExecutor executor;

  private boolean onFollowUpProcessing;
  private StatusEvent event;

  FollowUpController( SystemExecutor executor ) {
    verifyNotNull( executor, "executor" );

    this.followUps = new HashMap<>();
    this.executor = executor;
  }

  void process( StatusEvent event, Map<Scope, Scene> scenes, Runnable preparations, Runnable actions ) {
    this.event = event;
    try {
      doProcessing( event, scenes, preparations, actions );
    } finally {
      this.event = null;
    }
  }

  void schedule(
    Map<Scope, Scene> scenes, long delay, TimeUnit unit, Runnable followUp, Consumer<StatusEvent> callback )
  {
    verifySceneActivationBoundary();

    FollowUpKey key = new FollowUpKey( scenes, event );
    if( !followUps.containsKey( key ) ) {
      followUps.put( key, new ArrayList<>() );
    }
    followUps.get( key ).add( followUp );
    executor.schedule( () -> executeFollowUp( callback, key ), delay, unit );
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

  private void doProcessing( StatusEvent event, Map<Scope, Scene> scenes, Runnable preparations, Runnable actions ) {
    if( onFollowUpProcessing ) {
      doFollowUpProcessing( event, scenes, actions );
    } else {
      preparations.run();
      actions.run();
    }
  }

  private void doFollowUpProcessing( StatusEvent event, Map<Scope, Scene> scenes, Runnable actions ) {
    FollowUpKey key = new FollowUpKey( scenes, event );
    if( followUps.containsKey( key ) ) {
      followUps.get( key ).forEach( followUpHandler -> followUpHandler.run() );
      actions.run();
    }
  }
}