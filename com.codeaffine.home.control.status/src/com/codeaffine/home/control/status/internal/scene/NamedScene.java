package com.codeaffine.home.control.status.internal.scene;

import static com.codeaffine.home.control.status.SceneSelector.loadScene;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.EmptyScene;
import com.codeaffine.home.control.status.Scene;

class NamedScene {

  private final Class<? extends Scene> sceneType;
  private final Context context;

  NamedScene( Context context, Class<? extends Scene> sceneType ) {
    this.context = verifyNotNull( context, "context" );
    this.sceneType = verifyNotNull( sceneType, "sceneType" );
  }

  public boolean isActive() {
    return sceneType != EmptyScene.class;
  }

  public Class<? extends Scene> getSceneType() {
    return sceneType;
  }

  @Override
  public String toString() {
    return loadScene( context, sceneType ).getName();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + sceneType.hashCode();
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
    NamedScene other = ( NamedScene )obj;
    if( !sceneType.equals( other.sceneType ) )
      return false;
    return true;
  }
}