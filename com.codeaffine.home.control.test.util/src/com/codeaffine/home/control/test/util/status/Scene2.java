package com.codeaffine.home.control.test.util.status;

import com.codeaffine.home.control.status.Scene;

public class Scene2 implements Scene {

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {}
}