package com.codeaffine.home.control.status.supplier;

import java.util.Map;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.status.StatusSupplier;

public interface NamedSceneSupplier extends StatusSupplier<NamedSceneSelection> {

  public final static Scope DEFAULT_SCOPE = new NamedSceneDefaultScope();
  public final static String OFF = "OFF";
  public final static String SCENE_UNSELECT_SUFFIX = "->" + OFF;

  public interface NamedSceneConfiguration {
    public void configureNamedScenes( Map<String, Class<? extends Scene>> nameToSceneTypeMapping );
  }

  static class NamedSceneDefaultScope implements Scope {

    @Override
    public int compareTo( Scope o ) {
      return o instanceof NamedSceneDefaultScope ? 0 : -1;
    }

    @Override
    public int getOrdinal() {
      return -1;
    }

    @Override
    public String getName() {
      return getClass().getSimpleName();
    }
  }

}