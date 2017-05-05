package com.codeaffine.home.control.admin.ui.preference.info;

public class AttributeAction {

  private final AttributeActionType type;
  private final Runnable action;

  public AttributeAction( AttributeActionType type, Runnable action ) {
    this.type = type;
    this.action = action;
  }

  public AttributeActionType getType() {
    return type;
  }

  public void run() {
    action.run();
  }
}