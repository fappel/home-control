package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.SceneSelectorImpl.getScene;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.control.SceneSelector.Branch;
import com.codeaffine.home.control.application.control.SceneSelector.NodeCondition;
import com.codeaffine.home.control.application.control.StatusProvider;

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

    Node<Object> node = new Node<>();
    node.setStatusProvider( () -> new Object() );
    node.setPredicate( object -> true );
    node.setScene( getScene( context, sceneType ) );
    parent.setLevelEnd( node );
    return new BranchImpl( node, context );
  }

  @Override
  public <S, T extends StatusProvider<S>> NodeCondition<S> otherwiseWhenStatusOf( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setSuccessor( result.getNode() );
    return result;
  }
}