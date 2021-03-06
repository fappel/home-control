package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.status.SceneSelector.Scope;

public class HomeScope implements Scope {

  private static int ordinalCount = 0;
  static final String GLOBAL_NAME = "GLOBAL";
  static final int GLOBAL_ORDINAL = ordinalCount++;
  static final String BED_ROOM_NAME = "BED_ROOM";
  static final int BED_ROOM_ORDINAL = ordinalCount++;
  static final String KITCHEN_NAME = "KITCHEN";
  static final int KITCHEN_ORDINAL = ordinalCount++;
  static final String LIVING_ROOM_NAME = "LIVING_ROOM";
  static final int LIVING_ROOM_ORDINAL = ordinalCount++;
  static final String BATH_ROOM_NAME = "BATH";
  static final int BATH_ROOM_ORDINAL = ordinalCount++;

  public final static HomeScope BATH_ROOM = new HomeScope( BATH_ROOM_NAME, BATH_ROOM_ORDINAL );
  public final static HomeScope LIVING_ROOM = new HomeScope( LIVING_ROOM_NAME, LIVING_ROOM_ORDINAL );
  public final static HomeScope BED_ROOM = new HomeScope( BED_ROOM_NAME, BED_ROOM_ORDINAL );
  public final static HomeScope KITCHEN = new HomeScope( KITCHEN_NAME, KITCHEN_ORDINAL );
  public final static HomeScope GLOBAL = new HomeScope( GLOBAL_NAME, GLOBAL_ORDINAL );

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

  public static HomeScope[] values() {
    return new HomeScope[] { GLOBAL, BED_ROOM, KITCHEN, LIVING_ROOM, BATH_ROOM };
  }
}