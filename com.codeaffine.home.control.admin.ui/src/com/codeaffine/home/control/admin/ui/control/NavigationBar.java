package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.swt.SWT.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.home.control.admin.ui.api.ActionSupplier;

public class NavigationBar {

  private final List<NavigationBarItem> items;
  private final ActionSupplier actions;
  private final Composite control;

  public NavigationBar( Composite parent, ActionSupplier actionSupplier ) {
    verifyNotNull( actionSupplier, "actionSupplier" );
    verifyNotNull( parent, "parent" );

    this.control = new Composite( parent, NONE );
    this.control.setLayout( new FillLayout( HORIZONTAL ) );
    this.items = new ArrayList<>();
    this.actions = actionSupplier;
  }

  public Control getControl() {
    return control;
  }

  public void newItem( Object actionId, String label ) {
    verifyNotNull( actionId, "actionId" );
    verifyNotNull( label, "label" );

    items.add( new NavigationBarItem( this, label, actionId ) );
  }

  public void selectItem( Object actionId ) {
    verifyNotNull( actionId, "actionId" );

    findItem( actionId ).ifPresent( item -> item.select() );
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

  private Optional<NavigationBarItem> findItem( Object actionId ) {
    return items.stream().filter( item -> actionId.equals( item.getActionId() ) ).findAny();
  }
}