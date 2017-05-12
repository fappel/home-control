package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.ADDITION_INFO_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptor;
import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class CellEditorControlUtilTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();
  private Shell editorControl;

  @Before
  public void setUp() {
    editorControl = displayHelper.createShell();
  }

  @Test
  public void setElementPartKey() {
    CellEditorControlUtil.setElementPartKey( editorControl, ADDITION_INFO_VALUE );
    String actual = CellEditorControlUtil.getElementPartKey( editorControl );

    assertThat( actual ).isEqualTo( ADDITION_INFO_VALUE );
  }

  @Test
  public void setCellEditor() {
    CellEditor expected = mock( CellEditor.class );

    CellEditorControlUtil.setCellEditor( editorControl, expected );
    CellEditor actual = CellEditorControlUtil.getCellEditor( editorControl );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void setAttributeDescriptor() {
    AttributeDescriptor expected = mock( AttributeDescriptor.class );

    CellEditorControlUtil.setAttributeDescriptor( editorControl, expected );
    AttributeDescriptor actual = CellEditorControlUtil.getAttributeDescriptor( editorControl );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void deactivateEventHandler() {
    Listener listener = mock( Listener.class );
    editorControl.addListener( SWT.Show, listener );

    CellEditorControlUtil.deactivateEventHandler( editorControl, SWT.Show );
    editorControl.open();

    verify( listener, never() ).handleEvent( any() );
  }
}
