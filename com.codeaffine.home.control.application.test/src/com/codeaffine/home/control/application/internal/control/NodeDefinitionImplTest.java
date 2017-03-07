package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.NodeType.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.control.SceneSelector.Branch;
import com.codeaffine.home.control.application.control.SceneSelector.NodeCondition;
import com.codeaffine.home.control.application.test.MyStatus;
import com.codeaffine.home.control.application.test.MyStatusProvider;
import com.codeaffine.home.control.application.test.Scene1;
import com.codeaffine.home.control.test.util.context.TestContext;

public class NodeDefinitionImplTest {

  private NodeDefinitionImpl nodeDefinition;
  private MyStatusProvider statusProvider;
  private Node<MyStatus> node;

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    statusProvider = new MyStatusProvider();
    context.set( MyStatusProvider.class, statusProvider );
    node = new Node<>();
    nodeDefinition = new NodeDefinitionImpl( node, context );
  }

  @Test
  public void whenStatusOf() {
    NodeCondition<MyStatus> actual = nodeDefinition.whenStatusOf( MyStatusProvider.class );

    assertThat( actual ).isNotNull();
    assertThat( node.getNext() ).isNotNull();
    assertThat( node.nextIs( CHILD ) ).isTrue();
    assertThat( node.getNext().getStatusProvider() ).isSameAs( statusProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void whenStatusOfWithNullAsStatusProviderArgument() {
    nodeDefinition.whenStatusOf( null );
  }

  @Test
  public void thenSelect() {
    Branch actual = nodeDefinition.thenSelect( Scene1.class );

    assertThat( actual ).isNotNull();
    assertThat( node.getScene() ).isInstanceOf( Scene1.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void thenSelectWithNullAsSceneTypeArgument() {
    nodeDefinition.thenSelect( null );
  }

  @Test
  public void or() {
    NodeCondition<MyStatus> actual = nodeDefinition.or( MyStatusProvider.class );

    assertThat( actual ).isNotNull();
    assertThat( node.getNext() ).isNotNull();
    assertThat( node.nextIs( OR ) ).isTrue();
    assertThat( node.getNext().getStatusProvider() ).isSameAs( statusProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void orWithNullAsStatusProviderArgument() {
    nodeDefinition.or( null );
  }

  @Test
  public void and() {
    NodeCondition<MyStatus> actual = nodeDefinition.and( MyStatusProvider.class );

    assertThat( actual ).isNotNull();
    assertThat( node.getNext() ).isNotNull();
    assertThat( node.nextIs( AND ) ).isTrue();
    assertThat( node.getNext().getStatusProvider() ).isSameAs( statusProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void andWithNullAsStatusProviderArgument() {
    nodeDefinition.and( null );
  }
}