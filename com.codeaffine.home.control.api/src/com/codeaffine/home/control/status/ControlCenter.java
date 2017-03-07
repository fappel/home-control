package com.codeaffine.home.control.status;

public interface ControlCenter {

  public interface ControlCenterConfiguration {
    void configureSceneSelection( SceneSelector sceneSelector );
    void configureHomeControlOperations( ControlCenter controlCenter );
    void configureStatusProvider( StatusProviderRegistry statusProviderRegistry );
  }

  <T extends HomeControlOperation> void registerOperation( Class<T> operationType );
}