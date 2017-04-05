package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.status.SceneSelector.loadScene;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Function;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Branch;
import com.codeaffine.home.control.status.SceneSelector.NodeCondition;
import com.codeaffine.home.control.status.SceneSelector.NodeDefinition;
import com.codeaffine.home.control.status.StatusSupplier;

class NodeDefinitionImpl implements NodeDefinition {

  private final Context context;
  private final Node<?> parent;

  NodeDefinitionImpl( Node<?> parent, Context context ) {
    this.parent = parent;
    this.context = context;
  }

  @Override
  public <S, T extends StatusSupplier<S>> NodeCondition<S> whenStatusOf( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setChild( result.getNode() );
    return result;
  }

  @Override
  public <T extends Scene> Branch thenSelect( Class<T> sceneType ) {
    verifyNotNull( sceneType, "sceneType" );

    parent.setScene( loadScene( context, sceneType ) );
    return new BranchImpl( parent, context );
  }

  @Override
  public <S, T extends StatusSupplier<S>, U extends Scene> Branch
    thenSelect( Class<T> statusProviderType, Function<S, Class<U>> sceneProvider )
  {
    verifyNotNull( statusProviderType, "statusProviderType" );
    verifyNotNull( sceneProvider, "sceneProviderProviderType" );

    parent.setScene( new DynamicSceneProxy<>( context, context.get( statusProviderType ), sceneProvider ) );
    return new BranchImpl( parent, context );
  }

  @Override
  public <S, T extends StatusSupplier<S>> NodeCondition<S> or( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setOrConjunction( result.getNode() );
    return result;    }

  @Override
  public <S, T extends StatusSupplier<S>> NodeCondition<S> and( Class<T> statusProviderType ) {
    verifyNotNull( statusProviderType, "statusProviderType" );

    NodeConditionImpl<S> result = new NodeConditionImpl<>( context, statusProviderType );
    parent.setAndConjunction( result.getNode() );
    return result;
  }
}