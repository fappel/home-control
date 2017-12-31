package com.codeaffine.home.control.status.supplier;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;

public interface NamedSceneSelection {

  boolean isActive( Scope scope );

  Class<? extends Scene> getSceneType( Scope scope );
}