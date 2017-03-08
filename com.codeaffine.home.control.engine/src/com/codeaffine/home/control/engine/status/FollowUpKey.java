package com.codeaffine.home.control.engine.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Map;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.status.StatusEvent;

class FollowUpKey {

  private final Map<Scope, Scene> scenes;
  private final StatusEvent event;

  FollowUpKey( Map<Scope, Scene> scenes, StatusEvent event ) {
    verifyNotNull( scenes, "scenes" );
    verifyNotNull( event, "event" );

    this.scenes = scenes;
    this.event = event;
  }

  StatusEvent getEvent() {
    return event;
  }

  Map<Scope, Scene> getScenes() {
    return scenes;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + event.hashCode();
    result = prime * result + scenes.hashCode();
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    FollowUpKey other = ( FollowUpKey )obj;
    if( !event.equals( other.event ) )
      return false;
    if( !scenes.equals( other.scenes ) )
      return false;
    return true;
  }
}