package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.status.SceneSelector.loadScene;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Function;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusProvider;

class DynamicSceneProxy<S, T extends StatusProvider<S>, U extends Scene> implements Scene {

  private final Function<S, Class<U>> sceneProvider;
  private final T statusProvider;
  private final Context context;

  DynamicSceneProxy( Context context, T statusProvider, Function<S, Class<U>> sceneProvider ) {
    verifyNotNull( statusProvider, "statusProvider" );
    verifyNotNull( sceneProvider, "sceneProvider" );
    verifyNotNull( context, "context" );

    this.statusProvider = statusProvider;
    this.sceneProvider = sceneProvider;
    this.context = context;
  }

  @Override
  public String getName() {
    return getDynamicScene().getName();
  }

  @Override
  public void prepare() {
    getDynamicScene().prepare();
  }

  @Override
  public void close() {
    getDynamicScene().close();
  }

  private U getDynamicScene() {
    return loadScene( context, sceneProvider.apply( statusProvider.getStatus() ) );
  }
}