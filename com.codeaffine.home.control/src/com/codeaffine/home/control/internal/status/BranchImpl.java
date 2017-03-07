package com.codeaffine.home.control.internal.status;

import static com.codeaffine.home.control.internal.status.SceneSelectorImpl.getScene;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusProvider;
import com.codeaffine.home.control.status.SceneSelector.Branch;
import com.codeaffine.home.control.status.SceneSelector.NodeCondition;

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