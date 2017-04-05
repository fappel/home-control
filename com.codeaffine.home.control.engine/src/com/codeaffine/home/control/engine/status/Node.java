package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.Messages.*;
import static com.codeaffine.home.control.engine.status.NodeType.*;

import java.util.function.Predicate;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusSupplier;

class Node<S> {

  private StatusSupplier<S> statusProvider;
  private Predicate<S> predicate;
  private NextNode next;
  private Scene scene;

  public Scene evaluate() {
    if( predicate.test( statusProvider.getStatus() ) ) {
      if( nextIs( AND ) || nextIs( CHILD ) ) {
        return next.evaluate();
      }
      if( nextIs( OR ) ) {
        return getNextScene();
      }
      return scene;
    }
    return next.evaluate();
  }

  boolean nextIs( NodeType type ) {
    return hasNext() && next.is( type );
  }

  void setPredicate( Predicate<S> predicate ) {
    this.predicate = predicate;
  }

  Predicate<S> getPredicate() {
    return predicate;
  }

  void setStatusProvider( StatusSupplier<S> statusProvider ) {
    this.statusProvider = statusProvider;
  }

  StatusSupplier<S> getStatusProvider() {
    return statusProvider;
  }

  void setScene( Scene scene ) {
    this.scene = scene;
  }

  Scene getScene() {
    return scene;
  }

  void setChild( Node<?> child ) {
    setNextNode( new NextNode( child, CHILD ) );
  }

  void setSuccessor( Node<?> successor ) {
    setNextNode( new NextNode( successor, SUCCESSOR ) );
  }

  void setLevelEnd( Node<?> lastInLine ) {
    setNextNode( new NextNode( lastInLine, LEVEL_ENDING ) );
  }

  void setAndConjunction( Node<?> and ) {
    setNextNode( new NextNode( and, AND ) );
  }

  void setOrConjunction( Node<?> or ) {
    setNextNode( new NextNode( or, OR ) );
  }

  Node<?> getNext() {
    return next.node;
  }

  boolean hasNext() {
    return next != null;
  }

  private Scene getNextScene() {
    Node<?> current = this;
    Scene result = null;
    do {
      current = current.getNext();
      result = current.scene;
    } while( current.next != null && result == null );
    if( result == null ) {
      throw new IllegalStateException( ERROR_MISSING_SCENE_SELECTION );
    }
    return result;
  }

  private void setNextNode( NextNode nextNode ) {
    if( hasNext() ) {
      throw new IllegalStateException( ERROR_NEXT_NODE_ALREADY_EXISTS );
    }
    next = nextNode;
  }
}