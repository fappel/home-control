package com.codeaffine.home.control.application.status;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.internal.scene.NamedSceneDefaultSelection;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.Scene1;
import com.codeaffine.home.control.test.util.status.Scene2;
import com.codeaffine.test.util.lang.EqualsTester;

public class NamedSceneTest {

  private TestContext context;

  @Before
  public void setUp() {
    context = new TestContext();
  }

  @Test
  public void accessors() {
    NamedScene actual = new NamedScene( context, Scene1.class );

    assertThat( actual.getSceneType() ).isSameAs( Scene1.class );
    assertThat( actual.isActive() ).isTrue();
  }

  @Test
  public void isActiveWithDefaultSelection() {
    NamedScene namedScene = new NamedScene( context, NamedSceneDefaultSelection.class );

    boolean actual = namedScene.isActive();

    assertThat( actual ).isFalse();
  }

  @Test
  public void toStringImplementation() {
    NamedScene nameScene = new NamedScene( context, Scene1.class );

    String actual = nameScene.toString();

    assertThat( actual ).isEqualTo( context.get( Scene1.class ).getName() );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<NamedScene> tester = EqualsTester.newInstance( new NamedScene( context, Scene1.class ) );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new NamedScene( context, Scene1.class ), new NamedScene( context, Scene1.class ) );
    tester.assertNotEqual( new NamedScene( context, Scene1.class ), new NamedScene( context, Scene2.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsContextArgument() {
    new NamedScene( null, Scene1.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSceneTypeArgument() {
    new NamedScene( context, null );
  }
}