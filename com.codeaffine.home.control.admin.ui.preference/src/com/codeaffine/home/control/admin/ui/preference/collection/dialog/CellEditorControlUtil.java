package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static java.util.Arrays.stream;

import java.util.stream.Stream;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptor;

class CellEditorControlUtil {

  private static final String EDITOR_DATA_ATTRIBUTE_DESCRIPTOR = "attributeDescriptor";
  private static final String EDITOR_DATA_CELL_EDITOR = "cellEditor";
  private static final String EDITOR_DATA_ELEMENT_PART_KEY = "elementPartKey";

  static void setElementPartKey( Control editorControl, String elementPartKey ) {
    setEditorControlData( editorControl, EDITOR_DATA_ELEMENT_PART_KEY, elementPartKey );
  }

  static String getElementPartKey( Control editorControl ) {
    return getEditorControlData( editorControl, EDITOR_DATA_ELEMENT_PART_KEY, String.class );
  }

  static void setCellEditor( Control editorControl, CellEditor cellEditor ) {
    setEditorControlData( editorControl, EDITOR_DATA_CELL_EDITOR, cellEditor );
  }

  static CellEditor getCellEditor( Control editorControl ) {
    return getEditorControlData( editorControl, EDITOR_DATA_CELL_EDITOR, CellEditor.class );
  }

  static void setAttributeDescriptor( Control editorControl, AttributeDescriptor attributeDescriptor ) {
    setEditorControlData( editorControl, EDITOR_DATA_ATTRIBUTE_DESCRIPTOR, attributeDescriptor );
  }

  static AttributeDescriptor getAttributeDescriptor( Control editorControl ) {
    return getEditorControlData( editorControl, EDITOR_DATA_ATTRIBUTE_DESCRIPTOR, AttributeDescriptor.class );
  }

  static void deactivateEventHandler( Control editorControl, int ...eventTypes ) {
    stream( eventTypes ).forEach( eventType -> {
      Listener[] listeners = editorControl.getListeners( eventType );
      Stream.of( listeners ).forEach( listener -> editorControl.removeListener( eventType, listener ) );
    } );
  }

  private static void setEditorControlData( Control editorControl, String key, Object value ) {
    editorControl.setData( key, value );
  }

  private static <T> T getEditorControlData( Control editorControl, String key, Class<T> returnType ) {
    return returnType.cast( editorControl.getData( key ) );
  }
}