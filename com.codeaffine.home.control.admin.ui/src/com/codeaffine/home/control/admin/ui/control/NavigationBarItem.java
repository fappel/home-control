package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.Theme.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.rap.rwt.RWT.CUSTOM_VARIANT;
import static org.eclipse.swt.SWT.Selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class NavigationBarItem {

  private final NavigationBar navigationBar;
  private final Object actionId;
  private final Button button;

  NavigationBarItem( NavigationBar navigationBar, String label, Object actionId ) {
    verifyNotNull( navigationBar, "navigationBar" );
    verifyNotNull( actionId, "actionId" );
    verifyNotNull( label, "label" );

    this.button = new Button( ( Composite )navigationBar.getControl(), SWT.PUSH );
    this.navigationBar = navigationBar;
    this.actionId = actionId;
    initialize( label );
  }

  Object getActionId() {
    return actionId;
  }

  Control getControl() {
    return button;
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