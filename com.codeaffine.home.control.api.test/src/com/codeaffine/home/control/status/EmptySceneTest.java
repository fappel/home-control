package com.codeaffine.home.control.status;

import static com.codeaffine.home.control.status.Messages.INFO_NO_SCENE_SELECTED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.status.EmptyScene;

public class EmptySceneTest {

  @Test
  public void getName() {
    String actual = new EmptyScene().getName();

    assertThat( actual ).isEqualTo( INFO_NO_SCENE_SELECTED );
  }
}
