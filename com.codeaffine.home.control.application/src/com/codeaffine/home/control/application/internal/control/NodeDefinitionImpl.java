package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.control.SceneSelector.Branch;
import com.codeaffine.home.control.application.control.SceneSelector.NodeCondition;
import com.codeaffine.home.control.application.control.SceneSelector.NodeDefinition;
import com.codeaffine.home.control.application.control.StatusProvider;

class NodeDefinitionImpl implements NodeDefinition {

  private final Context context;
  private final Node<?> parent;

  NodeDefinitionImpl( Node<?> parent, Context context ) {
    this.parent = parent;
    this.context = context;
  }

  @Override
  public <S, T extends StatusProvider<S>> NodeCondition<S> whenStatusOf( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setChild( result.getNode() );
    return result;
  }

  @Override
  public <T extends Scene> Branch thenSelect( Class<T> sceneType ) {
    verifyNotNull( sceneType, "sceneType" );

    parent.setScene( SceneSelectorImpl.getScene( context, sceneType ) );
    return new BranchImpl( parent, context );
  }

  @Override
  public <S, T extends StatusProvider<S>> NodeCondition<S> or( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setOrConjunction( result.getNode() );
    return result;    }

  @Override
  public <S, T extends StatusProvider<S>> NodeCondition<S> and( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setAndConjunction( result.getNode() );
    return result;
  }
}