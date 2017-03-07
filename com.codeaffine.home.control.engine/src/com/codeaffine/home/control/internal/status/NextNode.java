package com.codeaffine.home.control.internal.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.status.Scene;

class NextNode {

  NodeType type;
  Node<?> node;

  NextNode( Node<?> node, NodeType type ) {
    verifyNotNull( node, "node" );
    verifyNotNull( type, "type" );

    this.node = node;
    this.type = type;
  }

  boolean is( NodeType type) {
    return this.type == type;
  }

  public Scene evaluate() {
    return node.evaluate();
  }
}