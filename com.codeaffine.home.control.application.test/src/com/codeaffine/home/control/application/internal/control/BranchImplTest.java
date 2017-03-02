package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.NodeType.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.control.SceneSelector.Branch;
import com.codeaffine.home.control.application.control.SceneSelector.NodeCondition;
import com.codeaffine.home.control.test.util.context.TestContext;

public class BranchImplTest {

  private MyStatusProvider myStatusProvider;
  private TestContext context;
  private BranchImpl branch;
  private Node<?> parent;

  @Before
  public void setUp() {
    parent = new Node<>();
    context = new TestContext();
    myStatusProvider = new MyStatusProvider();
    context.set( MyStatusProvider.class, myStatusProvider );
    branch = new BranchImpl( parent, context );
  }

  @Test
  public void otherwiseSelect() {
    Branch actual = branch.otherwiseSelect( Scene1.class );

    assertThat( actual ).isNotNull();
    assertThat( parent.nextIs( LEVEL_ENDING ) ).isTrue();
    assertThat( parent.getNext().evaluate() ).isInstanceOf( Scene1.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void otherwiseSelectWithNullAsSceneTypeArgument() {
    branch.otherwiseSelect( null );
  }

  @Test
  public void otherwiseWhenStatusOf() {
    NodeCondition<Status> actual = branch.otherwiseWhenStatusOf( MyStatusProvider.class );

    assertThat( actual ).isNotNull();
    assertThat( parent.nextIs( CHILD ) );
    assertThat( parent.getNext().getStatusProvider() ).isSameAs( myStatusProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void otherwiseWhenStatusOfWithNullAsStatusProviderType() {
    branch.otherwiseWhenStatusOf( null );
  }
}