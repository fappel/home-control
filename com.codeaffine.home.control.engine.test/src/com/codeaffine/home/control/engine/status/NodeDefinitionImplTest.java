package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.NodeType.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.SceneSelector.Branch;
import com.codeaffine.home.control.status.SceneSelector.NodeCondition;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatus;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;
import com.codeaffine.home.control.test.util.status.Scene1;

public class NodeDefinitionImplTest {

  private NodeDefinitionImpl nodeDefinition;
  private MyStatusProvider statusProvider;
  private Node<MyStatus> node;
  private TestContext context;

  @Before
  public void setUp() {
    context = new TestContext();
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
  public void thenSelectWithDynamicSceneSelection() {
    Branch actual = nodeDefinition.thenSelect( MyStatusProvider.class, status -> Scene1.class );

    assertThat( actual ).isNotNull();
    assertThat( node.getScene() ).isInstanceOf( DynamicSceneProxy.class );
    assertThat( node.getScene().getName() ).isEqualTo( context.get( Scene1.class ).getName() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void thenSelectWithDynamicSceneSelectionWithNullAsStatusProviderTypeArgument() {
    nodeDefinition.thenSelect( null, status -> Scene1.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void thenSelectWithDynamicSceneSelectionWithNullAsSceneProviderTypeArgument() {
    nodeDefinition.thenSelect( MyStatusProvider.class, null );
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