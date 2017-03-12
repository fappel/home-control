package com.codeaffine.home.control.application.status;

import static com.codeaffine.home.control.status.SceneSelector.loadScene;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.application.internal.scene.NamedSceneDefaultSelection;
import com.codeaffine.home.control.status.Scene;

public class NamedScene {

  private final Class<? extends Scene> sceneType;
  private final Context context;

  public NamedScene( Context context, Class<? extends Scene> sceneType ) {
    verifyNotNull( sceneType, "sceneType" );
    verifyNotNull( context, "context" );

    this.context = context;
    this.sceneType = sceneType;
  }

  public boolean isActive() {
    return !sceneType.isAssignableFrom( NamedSceneDefaultSelection.class );
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