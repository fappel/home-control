package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.status.SceneSelector.loadScene;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Function;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Branch;
import com.codeaffine.home.control.status.SceneSelector.NodeCondition;
import com.codeaffine.home.control.status.StatusProvider;

class BranchImpl implements Branch {

  private final Context context;
  private final Node<?> parent;

  BranchImpl( Node<?> parent, Context context ) {
    this.context = context;
    this.parent = parent;
  }

  @Override
  public <T extends Scene> Branch otherwiseSelect( Class<T> sceneType ) {
    verifyNotNull( sceneType, "sceneType" );

    Node<Object> node = prepareNode( loadScene( context, sceneType ) );
    return new BranchImpl( node, context );
  }

  @Override
  public <S, T extends StatusProvider<S>, U extends Scene> Branch
    otherwiseSelect( Class<T> statusProviderType, Function<S, Class<U>> sceneProvider )
  {
    verifyNotNull( sceneProvider, "sceneProviderProviderType" );
    verifyNotNull( statusProviderType, "statusProviderType" );

    T statusProvider = context.get( statusProviderType );
    Node<Object> node = prepareNode( new DynamicSceneProxy<>( context, statusProvider, sceneProvider ) );
    return new BranchImpl( node, context );
  }

  @Override
  public <S, T extends StatusProvider<S>> NodeCondition<S> otherwiseWhenStatusOf( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setSuccessor( result.getNode() );
    return result;
  }

  private <T extends Scene> Node<Object> prepareNode( Scene scene ) {
    Node<Object> result = new Node<>();
    result.setStatusProvider( () -> new Object() );
    result.setPredicate( object -> true );
    result.setScene( scene );
    parent.setLevelEnd( result );
    return result;
  }
}