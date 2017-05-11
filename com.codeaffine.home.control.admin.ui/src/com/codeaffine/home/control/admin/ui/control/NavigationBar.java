package com.codeaffine.home.control.admin.ui.control;

import static org.eclipse.swt.SWT.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class NavigationBar {

  private final List<NavigationBarItem> items;
  private final ActionMap actions;
  private final Composite control;

  public NavigationBar( Composite parent, ActionMap actions ) {
    this.control = new Composite( parent, NONE );
    this.control.setLayout( new FillLayout( HORIZONTAL ) );
    this.items = new ArrayList<>();
    this.actions = actions;
  }

  public Control getControl() {
    return control;
  }

  public void newItem( String label, Object actionId ) {
    items.add( new NavigationBarItem( this, label, actionId ) );
  }

  public void selectItem( Object actionId ) {
    items.stream().filter( item -> actionId.equals( item.getActionId() ) ).findAny().ifPresent( item -> item.select() );
  }

  Runnable getAction( Object actionId ) {
    return actions.getAction( actionId );
  }

  void addItem( NavigationBarItem navigationBarItem ) {
    items.add( navigationBarItem );
  }

  void unselectItems() {
    items.forEach( item -> item.unselect() );
  }
}