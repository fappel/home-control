package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.AddElementDialogUtil.asyncExec;
import static org.eclipse.swt.SWT.*;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

class EventHandlerRegistration {

  static void registerEventHandlers( AddElementDialog dialog ) {
    dialog.getElementEditorControls().forEach( control -> {
      control.addListener( KeyDown, evt -> closeShellOnEscapeKey( evt, dialog ) );
      control.addListener( Modify, evt -> handleCellEditorModified( evt, dialog ) );
    } );
    dialog.getCancelButton().addListener( Selection, evt -> dialog.close( CANCEL ) );
    dialog.getOkButton().addListener( Selection, evt -> dialog.close( OK ) );
    dialog.getOkButton().addListener( DefaultSelection, evt -> dialog.close( OK ) );
  }

  private static void closeShellOnEscapeKey( Event evt, AddElementDialog dialog ) {
    if( evt.keyCode == 27 ) {
      dialog.close( CANCEL );
    }
  }

  private static void handleCellEditorModified( Event evt, AddElementDialog dialog ) {
    asyncExec( () -> new CellEditorModifiedHandler( dialog ).accept( ( Control )evt.widget ) );
  }
}