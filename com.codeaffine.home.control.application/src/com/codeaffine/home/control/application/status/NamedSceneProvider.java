package com.codeaffine.home.control.application.status;

import java.util.Map;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusProvider;

public interface NamedSceneProvider extends StatusProvider<NamedScene> {

  public final static String OFF = "OFF";

  public interface NamedSceneConfiguration {
    void configureNamedScenes( Map<String, Class<? extends Scene>> nameToSceneTypeMapping );
  }
}