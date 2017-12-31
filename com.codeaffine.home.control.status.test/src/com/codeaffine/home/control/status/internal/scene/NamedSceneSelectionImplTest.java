package com.codeaffine.home.control.status.internal.scene;

import static com.codeaffine.home.control.test.util.status.MyScope.LOCAL;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.EmptyScene;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.test.util.lang.EqualsTester;

public class NamedSceneSelectionImplTest {

  private NamedSceneSelectionImpl selection;
  private TestContext context;

  static class LocalScene implements Scene {

    @Override
    public String getName() {
      return getClass().getSimpleName();
    }

    @Override
    public Optional<Scope> getScope() {
      return Optional.of( LOCAL );
    }
  }

  @Before
  public void setUp() {
    context = new TestContext();
    selection = new NamedSceneSelectionImpl( context );
  }

  @Test
  public void select() {
    selection.select( LOCAL, new NamedScene( context, LocalScene.class ) );

    assertThat( selection.isActive( LOCAL ) ).isTrue();
    assertThat( selection.getSceneType( LOCAL ) ).isSameAs( LocalScene.class );
  }

  @Test
  public void unselect() {
    selection.select( LOCAL, new NamedScene( context, LocalScene.class ) );

    selection.unselect( LOCAL );

    assertThat( selection.isActive( LOCAL ) ).isFalse();
    assertThat( selection.getSceneType( LOCAL ) ).isSameAs( EmptyScene.class );
  }

  @Test
  public void unselectAll() {
    selection.select( LOCAL, new NamedScene( context, LocalScene.class ) );

    selection.unselectAll();

    assertThat( selection.isActive( LOCAL ) ).isFalse();
    assertThat( selection.getSceneType( LOCAL ) ).isSameAs( EmptyScene.class );
  }

  @Test
  public void copy() {
    selection.select( LOCAL, new NamedScene( context, LocalScene.class ) );

    NamedSceneSelectionImpl actual = selection.copy();

    assertThat( actual )
      .isEqualTo( selection )
      .isNotSameAs( selection );
  }

  @Test
  public void equalsAndHashcode() {
    NamedSceneSelectionImpl selection1 = new NamedSceneSelectionImpl( context );
    selection1.select( LOCAL, new NamedScene( context, LocalScene.class ) );
    NamedSceneSelectionImpl selection2 = new NamedSceneSelectionImpl( context );
    selection2.select( LOCAL, new NamedScene( context, LocalScene.class ) );

    EqualsTester<NamedSceneSelectionImpl> tester = EqualsTester.newInstance( selection );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new NamedSceneSelectionImpl( context ), new NamedSceneSelectionImpl( context ) );
    tester.assertEqual( selection1, selection1 );
    tester.assertEqual( selection1, selection2 );
    tester.assertNotEqual( selection, selection1 );
  }

  @Test
  public void selectIfNoScopedSelectionWasSet() {
    assertThat( selection.isActive( LOCAL ) ).isFalse();
    assertThat( selection.getSceneType( LOCAL ) ).isSameAs( EmptyScene.class );
  }

  @Test(expected = IllegalArgumentException.class)
  public void isActiveWithNullAsScopeArgument() {
    selection.isActive( null );
  }

  @Test(expected = IllegalArgumentException.class)
  public void getSceneType() {
    selection.getSceneType( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void selectWithNullAsScopeArgument() {
    selection.select( null, new NamedScene( new TestContext(), LocalScene.class ) );
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectWithNullAsSceneTypeArgument() {
    selection.select( LOCAL, null );
  }

  @Test(expected = IllegalArgumentException.class)
  public void unselectWithNullAsScopeArgument() {
    selection.unselect( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void contstructWithNullAsContextArgument() {
    new NamedSceneSelectionImpl( null );
  }
}