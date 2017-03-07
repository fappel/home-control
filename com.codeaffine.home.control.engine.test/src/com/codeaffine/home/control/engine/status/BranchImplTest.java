package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.NodeType.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.engine.status.BranchImpl;
import com.codeaffine.home.control.engine.status.Node;
import com.codeaffine.home.control.status.SceneSelector.Branch;
import com.codeaffine.home.control.status.SceneSelector.NodeCondition;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatus;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;
import com.codeaffine.home.control.test.util.status.Scene1;

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
    NodeCondition<MyStatus> actual = branch.otherwiseWhenStatusOf( MyStatusProvider.class );

    assertThat( actual ).isNotNull();
    assertThat( parent.nextIs( CHILD ) );
    assertThat( parent.getNext().getStatusProvider() ).isSameAs( myStatusProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void otherwiseWhenStatusOfWithNullAsStatusProviderType() {
    branch.otherwiseWhenStatusOf( null );
  }
}