package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.CellEditorControlUtil.*;

import java.util.function.Consumer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptor;

class CellEditorModifiedHandler implements Consumer<Control> {

  private final AddElementDialog dialog;

  CellEditorModifiedHandler( AddElementDialog dialog ) {
    this.dialog = dialog;
  }

  @Override
  public void accept( Control editorControl ) {
    CellEditor editor = getCellEditor( editorControl );
    if( editor.getErrorMessage() == null ) {
      dialog.setValidationText( "" );
      AttributeDescriptor descriptor = getAttributeDescriptor( editorControl );
      dialog.putAdditionInfoEntry( getElementPartKey( editorControl ), descriptor.convertToValue( editor.getValue() ) );
    } else {
      dialog.setValidationText( editor.getErrorMessage() );
      dialog.putAdditionInfoEntry( getElementPartKey( editorControl ), null );
    }
    dialog.updateControlEnablement();
  }
}