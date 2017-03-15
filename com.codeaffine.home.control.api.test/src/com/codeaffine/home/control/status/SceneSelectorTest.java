package com.codeaffine.home.control.status;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.Scene1;

public class SceneSelectorTest {

  static class Bean {}

  static class TestScene implements Scene {

    final Bean injected;

    TestScene( Bean toInject ) {
      this.injected = toInject;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public void prepare() {}
  }

  @Test
  public void loadScene() {
    TestContext context = new TestContext();
    Bean toInject = new Bean();
    context.set( Bean.class, toInject );

    TestScene actual = SceneSelector.loadScene( context, TestScene.class );

    assertThat( actual ).isNotNull();
    assertThat( actual.injected ).isSameAs( toInject );
    assertThat( context.get( TestScene.class ) ).isNotNull();
  }

  @Test
  public void loadSceneMoreThanOnce() {
    TestContext context = new TestContext();

    Scene1 first = SceneSelector.loadScene( context, Scene1.class );
    Scene1 second = SceneSelector.loadScene( context, Scene1.class );

    assertThat( first )
      .isSameAs( second )
      .isNotNull();
    assertThat( context.get( Scene1.class ) ).isNotNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void loadSceneWithNullAsContextArgument() {
    SceneSelector.loadScene( null, Scene1.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void loadSceneWithNullAsSceneTypeArgument() {
    SceneSelector.loadScene( new TestContext(), null );
  }
}