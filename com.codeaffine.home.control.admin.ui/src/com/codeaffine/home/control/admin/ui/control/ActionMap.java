package com.codeaffine.home.control.admin.ui.control;

import java.util.HashMap;
import java.util.Map;

public class ActionMap {

  private final Map<Object, Runnable> actions;

  public ActionMap() {
    actions = new HashMap<>();
  }

  public Runnable getAction( Object actionId ) {
    return actions.get( actionId );
  }

  public void putAction( Object actionId, Runnable action ) {
    actions.put( actionId, action );
  }
}
