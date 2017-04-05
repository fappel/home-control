package com.codeaffine.home.control.status.supplier;

import java.util.Map;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusSupplier;

public interface NamedSceneSupplier extends StatusSupplier<NamedScene> {

  public final static String OFF = "OFF";

  public interface NamedSceneConfiguration {
    public void configureNamedScenes( Map<String, Class<? extends Scene>> nameToSceneTypeMapping );
  }
}