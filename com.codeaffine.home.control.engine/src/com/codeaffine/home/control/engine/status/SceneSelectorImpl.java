package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.Messages.*;
import static com.codeaffine.home.control.engine.status.NodeType.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.*;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector;
import com.codeaffine.home.control.status.StatusProvider;

public class SceneSelectorImpl implements SceneSelector {

  private final Map<Scope, Node<?>> scopes;
  private final Context context;
  private final Logger logger;

  private Map<Scope, Scene> selection;

  public SceneSelectorImpl( Context context, Logger logger ) {
    this.scopes = new HashMap<>();
    this.context = context;
    this.logger = logger;
  }

  @Override
  public <S> NodeCondition<S> whenStatusOf( Scope scope, Class<? extends StatusProvider<S>> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );
    verifyNotNull( scope, "scope" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    scopes.put( scope, result.getNode() );
    return result;
  }

  public Map<Scope, Scene> select() {
    Map<Scope, Scene> oldSelection = selection;
    selection = evaluate();
    if( oldSelection == null || !oldSelection.equals( selection ) ) {
      logger.info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( selection ) );
    }
    return new HashMap<>( selection );
  }

  void validate() {
    scopes.forEach( ( scope, node ) -> validate( node ) );
  }

  static String computeSelectedScenesInfo( Map<Scope, Scene> selection ) {
    return selection
      .keySet()
      .stream()
      .map( scope -> selection.get( scope ).getName() + " [ " + scope.getName() + " ]" )
      .collect( joining( ", ", "[ ", " ]" ) );
  }

  static <T extends Scene> T getScene( Context context, Class<T> sceneType ) {
    T result = context.get( sceneType );
    if( result == null ) {
      result = context.create( sceneType );
      context.set( sceneType, result );
    }
    return result;
  }

  private static void validate( Node<?> node ) {
    Node<?> current = node;
    int level = 1;
    boolean done = false;
    do {
      if( current.nextIs( CHILD ) ) {
        level++;
      }
      if( current.nextIs( LEVEL_ENDING ) ) {
        level--;
      }
      done = !current.hasNext();
      if( !done ) {
        current = current.getNext();
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

  private Map<Scope, Scene> evaluate() {
    return scopes
      .keySet()
      .stream()
      .sorted()
      .collect( toMap( scope -> scope, scope -> scopes.get( scope ).evaluate() ) );
  }
}