package com.codeaffine.home.control.status;

import com.codeaffine.home.control.Context;

public interface ControlCenter {

  public interface ControlCenterConfiguration {
    void configureSceneSelection( SceneSelector sceneSelector );
    void configureHomeControlOperations( ControlCenter controlCenter );
    void configureStatusSupplier( StatusSupplierRegistry statusSupplierRegistry );
  }

  <T extends HomeControlOperation> void registerOperation( Class<T> operationType );
  Context getContext();
}