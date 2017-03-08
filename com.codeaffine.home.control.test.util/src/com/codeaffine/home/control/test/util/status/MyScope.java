package com.codeaffine.home.control.test.util.status;

import com.codeaffine.home.control.status.SceneSelector.Scope;

public class MyScope implements Scope {

  public final static Scope GLOBAL = new MyScope( "GLOBAL", 0 );
  public final static Scope LOCAL = new MyScope( "LOCAL", 1 );

  private final String name;
  private final int ordinal;

  private MyScope( String name, int ordinal ) {
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