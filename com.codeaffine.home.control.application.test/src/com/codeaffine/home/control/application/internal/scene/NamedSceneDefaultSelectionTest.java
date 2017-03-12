package com.codeaffine.home.control.application.internal.scene;

import static com.codeaffine.home.control.application.internal.scene.Messages.INFO_NO_SCENE_SELECTED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class NamedSceneDefaultSelectionTest {

  @Test
  public void getName() {
    String actual = new NamedSceneDefaultSelection().getName();

    assertThat( actual ).isEqualTo( INFO_NO_SCENE_SELECTED );
  }
}
