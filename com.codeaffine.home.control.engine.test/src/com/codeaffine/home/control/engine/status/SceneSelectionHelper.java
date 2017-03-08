package com.codeaffine.home.control.engine.status;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

class SceneSelectionHelper {

  static Map<Scope, Scene> newSceneSelection( Scope scope, Scene scene ) {
    Map<Scope, Scene> result = new HashMap<>();
    result.put( scope, scene );
    return result;
  }
}