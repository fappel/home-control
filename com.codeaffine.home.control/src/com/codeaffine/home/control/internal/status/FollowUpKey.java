package com.codeaffine.home.control.internal.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusEvent;

class FollowUpKey {

  private final StatusEvent event;
  private final Scene scene;

  FollowUpKey( Scene scene, StatusEvent event ) {
    verifyNotNull( scene, "scene" );
    verifyNotNull( event, "event" );

    this.scene = scene;
    this.event = event;
  }

  StatusEvent getEvent() {
    return event;
  }

  Scene getScene() {
    return scene;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + event.hashCode();
    result = prime * result + scene.hashCode();
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
    if( !scene.equals( other.scene ) )
      return false;
    return true;
  }
}