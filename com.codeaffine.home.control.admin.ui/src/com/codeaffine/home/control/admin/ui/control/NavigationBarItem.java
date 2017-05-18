package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.api.Theme.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class NavigationBarItem {

  private final NavigationBar navigationBar;
  private final Object actionId;
  private final Anchor anchor;
  private final String label;

  NavigationBarItem( NavigationBar navigationBar, String label, Object actionId ) {
    verifyNotNull( navigationBar, "navigationBar" );
    verifyNotNull( actionId, "actionId" );
    verifyNotNull( label, "label" );

    this.anchor = new Anchor( ( Composite )navigationBar.getControl() );
    this.navigationBar = navigationBar;
    this.actionId = actionId;
    this.label = label;
    initialize();
  }

  Object getActionId() {
    return actionId;
  }

  Control getControl() {
    return anchor.getControl();
  }

  void select() {
    navigationBar.unselectItems();
    navigationBar.getAction( actionId ).run();
    anchor.configure( CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR_SELECTED, getFragment(), label );
  }

  void unselect() {
    anchor.configure( CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR, getFragment(), label );
  }

  private void initialize() {
    anchor.configure( CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR, getFragment(), label );
    anchor.addListener( SWT.Selection, evt -> select() );
  }

  private String getFragment() {
    return label.toLowerCase();
  }
}