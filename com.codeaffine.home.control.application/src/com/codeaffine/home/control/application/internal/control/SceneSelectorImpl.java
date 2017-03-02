package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.Messages.*;
import static com.codeaffine.home.control.application.internal.control.NodeType.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.control.SceneSelector;
import com.codeaffine.home.control.application.control.StatusProvider;

public class SceneSelectorImpl implements SceneSelector {

  private final Context context;

  private Node<?> root;

  public SceneSelectorImpl( Context context ) {
    this.context = context;
  }

  @Override
  public <S> NodeCondition<S> whenStatusOf( Class<? extends StatusProvider<S>> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    root = result.getNode();
    return result;
  }

  public Scene getDecision() {
    return root.evaluate();
  }

  static <T extends Scene> T getScene( Context context, Class<T> sceneType ) {
    T result = context.get( sceneType );
    if( result == null ) {
      result = context.create( sceneType );
      context.set( sceneType, result );
    }
    return result;
  }

  void validate() {
    Node<?> node = root;
    int level = 1;
    boolean done = false;
    do {
      if( node.nextIs( CHILD ) ) {
        level++;
      }
      if( node.nextIs( LEVEL_ENDING ) ) {
        level--;
      }
      done = !node.hasNext();
      if( !done ) {
        node = node.getNext();
      }
    } while( !done );
    if( level > 0 ) {
      Integer arg = Integer.valueOf( level );
      String message = format( ERROR_INVALID_SCENE_SELECTION_CONFIGURATION_MISSING_OTHERWISE_SELECT, arg );
      throw new IllegalStateException( message );
    }
    if( level < 0 ) {
      throw new IllegalStateException( ERROR_SUPERFLUOUS_OTHERWISE_SELECT_BRANCH_DETECTED );
    }
  }
}