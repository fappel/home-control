package com.codeaffine.home.control.status;

import static com.codeaffine.home.control.status.Messages.INFO_NO_SCENE_SELECTED;

public class EmptyScene implements Scene {

  @Override
  public String getName() {
    return INFO_NO_SCENE_SELECTED;
  }

  @Override
  public void prepare() {}
}