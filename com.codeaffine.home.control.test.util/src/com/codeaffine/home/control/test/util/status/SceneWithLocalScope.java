package com.codeaffine.home.control.test.util.status;

import static com.codeaffine.home.control.test.util.status.MyScope.LOCAL;

import java.util.Optional;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

public class SceneWithLocalScope implements Scene {

  @Override
  public Optional<Scope> getScope() {
    return Optional.of( LOCAL );
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}
