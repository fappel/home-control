package com.codeaffine.home.control.status.internal.scene;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.EmptyScene;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.status.supplier.NamedSceneSelection;

public class NamedSceneSelectionImpl implements NamedSceneSelection {

  private final Map<Scope, NamedScene> selections;
  private final NamedScene emptySelection;
  private final Context context;

  NamedSceneSelectionImpl( Context context ) {
    this( context, new HashMap<>());
  }

  private NamedSceneSelectionImpl( Context context, Map<Scope, NamedScene> selections ) {
    verifyNotNull( context, "context" );

    this.emptySelection = new NamedScene( context, EmptyScene.class );
    this.selections = selections;
    this.context = context;
  }

  @Override
  public boolean isActive( Scope scope ) {
    return getNamedScene( scope ).isActive();
  }

  @Override
  public Class<? extends Scene> getSceneType( Scope scope ) {
    return getNamedScene( scope ).getSceneType();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + selections.hashCode();
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj ) {
      return true;
    }
    if( obj == null ) {
      return false;
    }
    if( getClass() != obj.getClass() ) {
      return false;
    }
    NamedSceneSelectionImpl other = ( NamedSceneSelectionImpl )obj;
    if( !selections.equals( other.selections ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return selections.toString();
  }

  void select( Scope scope, NamedScene selection ) {
    verifyNotNull( scope, "scope" );
    verifyNotNull( selection, "selection" );

    selections.put( scope, selection );
  }

  void unselect( Scope scope ) {
    verifyNotNull( scope, "scope" );

    selections.remove( scope );
  }

  void unselectAll() {
    selections.clear();
  }

  NamedSceneSelectionImpl copy() {
    Map<Scope, NamedScene> content = new HashMap<>();
    content.putAll( selections );
    return new NamedSceneSelectionImpl( context, content );
  }

  private NamedScene getNamedScene( Scope scope ) {
    verifyNotNull( scope, "scope" );

    if( selections.containsKey( scope ) ) {
      return selections.get( scope );
    }
    return emptySelection;
  }
}