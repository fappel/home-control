package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.Theme.*;
import static org.eclipse.rap.rwt.RWT.CUSTOM_VARIANT;
import static org.eclipse.swt.SWT.Selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class NavigationBarItem {

  private final NavigationBar navigationBar;
  private final Object actionId;
  private final Button button;

  NavigationBarItem( NavigationBar navigationBar, String label, Object actionId ) {
    this.button = new Button( ( Composite )navigationBar.getControl(), SWT.PUSH );
    this.navigationBar = navigationBar;
    this.actionId = actionId;
    initialize( label );
  }

  Object getActionId() {
    return actionId;
  }

  void select() {
    navigationBar.unselectItems();
    navigationBar.getAction( actionId ).run();
    button.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR_SELECTED );
  }

  void unselect() {
    button.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR );
  }

  private void initialize( String label ) {
    button.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR );
    button.addListener( Selection, evt -> select() );
    button.setText( label );
  }
}