package com.codeaffine.home.control.engine.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.status.Node;
import com.codeaffine.home.control.engine.status.NodeConditionImpl;
import com.codeaffine.home.control.status.SceneSelector.NodeDefinition;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatus;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;

public class NodeConditionImplTest {

  private NodeConditionImpl<MyStatus> nodeCondition;
  private MyStatusProvider statusProvider;

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    statusProvider = new MyStatusProvider();
    context.set( MyStatusProvider.class, statusProvider );
    nodeCondition = new NodeConditionImpl<>( context, MyStatusProvider.class );
  }

  @Test
  public void construction() {
    Node<MyStatus> actual = nodeCondition.getNode();

    assertThat( actual.getStatusProvider() ).isSameAs( statusProvider );
  }

  @Test
  public void matches() {
    Predicate<MyStatus> predicate = status -> true;

    NodeDefinition actual = nodeCondition.matches( predicate );

    assertThat( nodeCondition.getNode().getPredicate() ).isSameAs( predicate );
    assertThat( actual ).isNotNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void matchesWithNullAsPredicateArgument() {
    nodeCondition.matches( null );
  }
}