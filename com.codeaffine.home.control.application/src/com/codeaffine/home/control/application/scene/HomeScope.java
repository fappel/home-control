package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.status.SceneSelector.Scope;

public class HomeScope implements Scope {

  private static int ordinalCount = 0;
  static final String GLOBAL_NAME = "GLOBAL";
  static final int GLOBAL_ORDINAL = ordinalCount++;
  static final String LIVING_ROOM_NAME = "LIVING_ROOM";
  static final int LIVING_ROOM_ORDINAL = ordinalCount++;

  public final static Scope LIVING_ROOM = new HomeScope( LIVING_ROOM_NAME, LIVING_ROOM_ORDINAL );
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