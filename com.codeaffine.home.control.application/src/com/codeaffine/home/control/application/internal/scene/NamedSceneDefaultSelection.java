package com.codeaffine.home.control.application.internal.scene;

import static com.codeaffine.home.control.application.internal.scene.Messages.INFO_NO_SCENE_SELECTED;

import com.codeaffine.home.control.status.Scene;

public class NamedSceneDefaultSelection implements Scene {

  @Override
  public String getName() {
    return INFO_NO_SCENE_SELECTED;
  }

  @Override
  public void activate() {}
}