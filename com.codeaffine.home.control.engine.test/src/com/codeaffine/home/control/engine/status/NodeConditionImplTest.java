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
import com.codeaffine.home.control.test.util.status.MyStatusSupplier;

public class NodeConditionImplTest {

  private NodeConditionImpl<MyStatus> nodeCondition;
  private MyStatusSupplier statusSupplier;

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    statusSupplier = new MyStatusSupplier();
    context.set( MyStatusSupplier.class, statusSupplier );
    nodeCondition = new NodeConditionImpl<>( context, MyStatusSupplier.class );
  }

  @Test
  public void construction() {
    Node<MyStatus> actual = nodeCondition.getNode();

    assertThat( actual.getStatusProvider() ).isSameAs( statusSupplier );
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
