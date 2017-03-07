package com.codeaffine.home.control.engine.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Predicate;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.StatusProvider;
import com.codeaffine.home.control.status.SceneSelector.NodeCondition;
import com.codeaffine.home.control.status.SceneSelector.NodeDefinition;

class NodeConditionImpl<S> implements NodeCondition<S> {

  private final Context context;
  private final Node<S> node;

  NodeConditionImpl( Context context, Class<? extends StatusProvider<S>> statusProviderType ) {
    this.context = context;
    this.node = new Node<>();
    node.setStatusProvider( context.get( statusProviderType ) );
  }

  @Override
  public NodeDefinition matches( Predicate<S> predicate ) {
    verifyNotNull( predicate, "predicate" );

    node.setPredicate( predicate );
    return new NodeDefinitionImpl( node, context );
  }

  public Node<S> getNode() {
    return node;
  }
}