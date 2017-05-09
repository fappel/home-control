package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.ADDITION_INFO_VALUE;
import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.CellEditorControlUtil.*;
import static org.eclipse.swt.SWT.NONE;
import static org.mockito.Mockito.*;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptor;
import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class CellEditorModifiedHandlerTest {

  private static final Object VALUE = new Object();
  private static final String INVALID_VALUE = "Invalid";

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private CellEditorModifiedHandler modifiedHandler;
  private AddElementDialog dialog;
  private Text editorControl;

  private AttributeDescriptor descriptor;

  private CellEditor cellEditor;

  @Before
  public void setUp() {
    Shell parent = displayHelper.createShell();
    dialog = mock( AddElementDialog.class );
    modifiedHandler = new CellEditorModifiedHandler( dialog );
    editorControl = new Text( parent, NONE );
    descriptor = mock( AttributeDescriptor.class );
    cellEditor = new TextCellEditor( parent );
    setAttributeDescriptor( editorControl, descriptor );
    setCellEditor( editorControl, cellEditor );
    setElementPartKey( editorControl, ADDITION_INFO_VALUE );
  }

  @Test
  public void accept() {
    cellEditor.setValue( VALUE.toString() );
    when( descriptor.convertToValue( VALUE.toString() ) ).thenReturn( VALUE );

    modifiedHandler.accept( editorControl );

    InOrder order = inOrder( dialog );
    order.verify( dialog ).setValidationText( "" );
    order.verify( dialog ).putAdditionInfoEntry( ADDITION_INFO_VALUE, VALUE );
    order.verify( dialog ).updateControlEnablement();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void acceptWithErrorMessage() {
    cellEditor.setValidator( value -> INVALID_VALUE );
    cellEditor.setValue( VALUE.toString() );

    modifiedHandler.accept( editorControl );

    InOrder order = inOrder( dialog );
    order.verify( dialog ).setValidationText( INVALID_VALUE );
    order.verify( dialog ).putAdditionInfoEntry( ADDITION_INFO_VALUE, null );
    order.verify( dialog ).updateControlEnablement();
    order.verifyNoMoreInteractions();
  }
}
