package com.codeaffine.home.control.internal.status;

import static com.codeaffine.home.control.internal.status.Messages.*;
import static com.codeaffine.home.control.internal.status.NodeType.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector;
import com.codeaffine.home.control.status.StatusProvider;

public class SceneSelectorImpl implements SceneSelector {

  private final Context context;
  private final Logger logger;

  private Node<?> root;
  private Scene selection;

  public SceneSelectorImpl( Context context, Logger logger ) {
    this.context = context;
    this.logger = logger;
  }

  @Override
  public <S> NodeCondition<S> whenStatusOf( Class<? extends StatusProvider<S>> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    root = result.getNode();
    return result;
  }

  public Scene select() {
    Scene oldSelection = selection;
    selection = root.evaluate();
    if( oldSelection != selection ) {
      logger.info( INFO_SELECTED_SCENE, selection.getClass().getSimpleName() );
    }
    return selection;
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