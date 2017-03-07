package com.codeaffine.home.control.engine.adapter;

import java.util.HashSet;
import java.util.Set;

public class ShutdownDispatcher {

  private final Set<Runnable> hooks;

  public ShutdownDispatcher() {
    hooks = new HashSet<>();
  }

  public void addShutdownHook( Runnable hook ) {
    hooks.add( hook );
  }

  public void removeShutdownHook( Runnable hook ) {
    hooks.remove( hook );
  }

  public void dispatch() {
    hooks.forEach( hook -> hook.run() );
  }
}