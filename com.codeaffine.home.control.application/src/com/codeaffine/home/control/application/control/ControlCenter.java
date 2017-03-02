package com.codeaffine.home.control.application.control;

public interface ControlCenter {

  public interface SceneSelectionConfigurer {
    void configure( SceneSelector sceneSelector );
  }

  <T extends ControlCenterOperation> void registerOperation( Class<T> operationType );
}