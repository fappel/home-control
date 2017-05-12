package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.AddElementDialogUtil.asyncExec;
import static org.eclipse.swt.SWT.*;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

class EventHandlerRegistration {

  static final int KEY_CODE_ESC = 27;

  private final CellEditorModifiedHandler cellEditorModifiedHandler;
  private final AddElementDialog dialog;

  EventHandlerRegistration( AddElementDialog dialog ) {
    this( dialog, new CellEditorModifiedHandler( dialog ) );
  }

  EventHandlerRegistration( AddElementDialog dialog, CellEditorModifiedHandler cellEditorModifiedHandler ) {
    this.cellEditorModifiedHandler = cellEditorModifiedHandler;
    this.dialog = dialog;
  }

  void initialize() {
    dialog.getElementEditorControls().forEach( control -> {
      control.addListener( KeyDown, evt -> closeShellOnEscapeKey( evt ) );
      control.addListener( Modify, evt -> handleCellEditorModified( evt ) );
    } );
    dialog.getCancelButton().addListener( Selection, evt -> dialog.close( CANCEL ) );
    dialog.getOkButton().addListener( Selection, evt -> dialog.close( OK ) );
    dialog.getOkButton().addListener( DefaultSelection, evt -> dialog.close( OK ) );
  }

  private void closeShellOnEscapeKey( Event evt ) {
    if( evt.keyCode == KEY_CODE_ESC ) {
      dialog.close( CANCEL );
    }
  }

  private void handleCellEditorModified( Event evt ) {
    asyncExec( () -> cellEditorModifiedHandler.accept( ( Control )evt.widget ) );
  }
}