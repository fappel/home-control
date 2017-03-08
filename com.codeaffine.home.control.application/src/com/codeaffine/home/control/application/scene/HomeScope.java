package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.status.SceneSelector.Scope;

public class HomeScope implements Scope {

  static final String HOTSPOT_NAME = "HOTSPOT";
  static final int HOTSPOT_ORDINAL = 1;
  static final String GLOBAL_NAME = "GLOBAL";
  static final int GLOBAL_ORDINAL = 0;

  public final static Scope HOTSPOT = new HomeScope( HOTSPOT_NAME, HOTSPOT_ORDINAL );
  public final static Scope GLOBAL = new HomeScope( GLOBAL_NAME, GLOBAL_ORDINAL );

  private final String name;
  private final int ordinal;

  private HomeScope( String name, int ordinal ) {
    this.name = name;
    this.ordinal = ordinal;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int compareTo( Scope o ) {
    return this.ordinal - o.getOrdinal();
  }

  @Override
  public int getOrdinal() {
    return ordinal;
  }
}