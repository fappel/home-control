package com.codeaffine.home.control.admin.ui.model;

import static com.codeaffine.home.control.admin.ui.model.Messages.ERROR_ACTION_ID_DOES_NOT_EXIST;
import static com.codeaffine.util.ArgumentVerification.*;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.admin.ui.api.ActionSupplier;

public class ActionMap implements ActionSupplier {

  private final Map<Object, Runnable> actions;

  public ActionMap() {
    actions = new HashMap<>();
  }

  @Override
  public Runnable getAction( Object actionId ) {
    verifyNotNull( actionId, "actionId" );
    verifyCondition( actions.containsKey( actionId ), ERROR_ACTION_ID_DOES_NOT_EXIST, actionId );

    return actions.get( actionId );
  }

  public void putAction( Object actionId, Runnable action ) {
    verifyNotNull( actionId, "actionId" );
    verifyNotNull( action, "action" );

    actions.put( actionId, action );
  }

  public void removeAction( Object actionId ) {
    verifyNotNull( actionId, "actionId" );

    actions.remove( actionId );
  }

  public boolean isEmpty() {
    return actions.isEmpty();
  }
}