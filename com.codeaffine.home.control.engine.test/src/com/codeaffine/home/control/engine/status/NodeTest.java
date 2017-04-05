package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.Messages.*;
import static com.codeaffine.home.control.test.util.status.MyStatus.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.status.Node;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusSupplier;
import com.codeaffine.home.control.test.util.status.MyStatus;
import com.codeaffine.home.control.test.util.status.MyStatusSupplier;
import com.codeaffine.home.control.test.util.status.Scene1;

public class NodeTest {

  private Node<MyStatus> node;
  private MyStatusSupplier statusSupplier;

  @Before
  public void setUp() {
    node = new Node<>();
    statusSupplier = new MyStatusSupplier();
  }

  @Test
  public void evaluateWithValidPrediate() {
    Scene1 expected = new Scene1();
    statusSupplier.setStatus( ONE );
    node.setStatusProvider( statusSupplier );
    node.setScene( expected );
    node.setPredicate( status -> status == ONE );

    Scene actual = node.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void evaluateWithValidPrediateAndChildWithValidPredicate() {
    Scene1 expected = new Scene1();
    statusSupplier.setStatus( ONE );
    node.setStatusProvider( statusSupplier );
    node.setPredicate( status -> status == ONE );
    Node<MyStatus> childNode = new Node<>();
    childNode.setStatusProvider( statusSupplier );
    childNode.setPredicate( status -> status == ONE );
    childNode.setScene( expected );
    node.setChild( childNode );

    Scene actual = node.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void evaluateWithValidPrediateAndAndConjunctionWithValidPredicate() {
    Scene1 expected = new Scene1();
    statusSupplier.setStatus( ONE );
    node.setStatusProvider( statusSupplier );
    node.setPredicate( status -> status == ONE );
    Node<MyStatus> andConjunction = new Node<>();
    andConjunction.setStatusProvider( statusSupplier );
    andConjunction.setPredicate( status -> status == ONE );
    andConjunction.setScene( expected );
    node.setAndConjunction( andConjunction );

    Scene actual = node.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void evaluateWithValidPrediateAndOrConjunctionWithValidPredicate() {
    Scene1 expected = new Scene1();
    statusSupplier.setStatus( ONE );
    node.setStatusProvider( statusSupplier );
    node.setPredicate( status -> status == ONE );
    Node<MyStatus> orConjunction = new Node<>();
    orConjunction.setStatusProvider( statusSupplier );
    orConjunction.setPredicate( status -> status == ONE );
    orConjunction.setScene( expected );
    node.setOrConjunction( orConjunction );

    Scene actual = node.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void evaluateWithValidPrediateAndOrConjunctionWithValidPredicateAndNestedConjunction() {
    Scene1 expected = new Scene1();
    statusSupplier.setStatus( ONE );
    node.setStatusProvider( statusSupplier );
    node.setPredicate( status -> status == ONE );
    Node<MyStatus> orConjunction = new Node<>();
    orConjunction.setStatusProvider( statusSupplier );
    orConjunction.setPredicate( status -> status == ONE );
    node.setOrConjunction( orConjunction );
    Node<MyStatus> andConjunction = new Node<>();
    andConjunction.setStatusProvider( statusSupplier );
    andConjunction.setPredicate( status -> status == ONE );
    andConjunction.setScene( expected );
    orConjunction.setAndConjunction( andConjunction );

    Scene actual = node.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void evaluateWithInvalidPrediateAndSuccessorWithValidPredicate() {
    Scene1 expected = new Scene1();
    statusSupplier.setStatus( ONE );
    node.setStatusProvider( statusSupplier );
    node.setPredicate( status -> status == TWO );
    Node<MyStatus> successor = new Node<>();
    successor.setStatusProvider( statusSupplier );
    successor.setPredicate( status -> status == ONE );
    successor.setScene( expected );
    node.setSuccessor( successor );

    Scene actual = node.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void evaluateWithInvalidPrediateAndOrConjunctionWithValidPredicate() {
    Scene1 expected = new Scene1();
    statusSupplier.setStatus( TWO );
    node.setStatusProvider( statusSupplier );
    node.setPredicate( status -> status == ONE );
    Node<MyStatus> orConjunction = new Node<>();
    orConjunction.setStatusProvider( statusSupplier );
    orConjunction.setPredicate( status -> status == TWO );
    orConjunction.setScene( expected );
    node.setOrConjunction( orConjunction );

    Scene actual = node.evaluate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void setNextNodeTwice() {
    node.setChild( new Node<>() );

    Throwable actual = thrownBy( () -> node.setChild( new Node<>() ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_NEXT_NODE_ALREADY_EXISTS );
  }

  @Test
  public void evaluateWithValidPrediateAndOrConjunctionWithValidPredicateButMissingSceneSelection() {
    statusSupplier.setStatus( TWO );
    node.setStatusProvider( statusSupplier );
    node.setPredicate( status -> status == TWO );
    Node<MyStatus> orConjunction = new Node<>();
    orConjunction.setStatusProvider( statusSupplier );
    orConjunction.setPredicate( status -> status == ONE );
    node.setOrConjunction( orConjunction );

    Throwable actual = thrownBy( () -> node.evaluate() );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_MISSING_SCENE_SELECTION );
  }

  @Test
  public void getStatusProvider() {
    node.setStatusProvider( statusSupplier );

    StatusSupplier<MyStatus> actual = node.getStatusProvider();

    assertThat( actual ).isSameAs( statusSupplier );
  }

  @Test
  public void getPredicate() {
    Predicate<MyStatus> expected = status -> true;
    node.setPredicate( expected );

    Predicate<MyStatus> actual = node.getPredicate();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void getScene() {
    Scene1 expected = new Scene1();
    node.setScene( expected );

    Scene actual = node.getScene();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void hasNext() {
    node.setChild( new Node<>() );

    boolean actual = node.hasNext();

    assertThat( actual ).isTrue();
  }

  @Test
  public void hasNextIfNoFollowupNodeWasAdded() {
    boolean actual = node.hasNext();

    assertThat( actual ).isFalse();
  }
}