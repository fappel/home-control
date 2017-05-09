package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.EventHandlerRegistration.KEY_CODE_ESC;
import static com.codeaffine.home.control.admin.ui.test.DisplayHelper.flushPendingEvents;
import static com.codeaffine.home.control.admin.ui.test.SWTEventHelper.trigger;
import static java.util.Arrays.asList;
import static org.eclipse.swt.SWT.*;
import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class EventHandlerRegistrationTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private CellEditorModifiedHandler modifiedHandler;
  private EventHandlerRegistration registration;
  private AddElementDialog dialog;
  private Text editorControl;
  private Button cancel;
  private Button ok;

  @Before
  public void setUp() {
    Shell shell = displayHelper.createShell();
    ok = new Button( shell, PUSH );
    cancel = new Button( shell, PUSH );
    editorControl = new Text( shell, NONE );
    dialog = stubDialog( ok, cancel, editorControl );
    modifiedHandler = mock( CellEditorModifiedHandler.class );
    registration = new EventHandlerRegistration( dialog, modifiedHandler );
    registration.initialize();
  }

  @Test
  public void escapeKeyDownOnEditorControl() {
    trigger( KeyDown ).withKeyCode( KEY_CODE_ESC ).on( editorControl );

    verify( dialog ).close( CANCEL );
  }

  @Test
  public void unrelatedKeyDownOnEditorControl() {
    trigger( KeyDown ).withKeyCode( 34 ).on( editorControl );

    verify( dialog, never() ).close( anyInt() );
  }

  @Test
  public void editorControlModified() {
    editorControl.setText( "text" );
    verify( modifiedHandler, never() ).accept( editorControl );
    flushPendingEvents();

    verify( modifiedHandler ).accept( editorControl );
  }

  @Test
  public void cancelButtonSelection() {
    trigger( Selection ).on( cancel );

    verify( dialog ).close( CANCEL );
  }

  @Test
  public void okButtonSelection() {
    trigger( Selection ).on( ok );

    verify( dialog ).close( OK );
  }

  @Test
  public void okButtonDefaultSelection() {
    trigger( DefaultSelection ).on( ok );

    verify( dialog ).close( OK );
  }

  private static AddElementDialog stubDialog( Button ok, Button cancel, Text editorControl ) {
    AddElementDialog result = mock( AddElementDialog.class );
    when( result.getOkButton() ).thenReturn( ok );
    when( result.getCancelButton() ).thenReturn( cancel );
    when( result.getElementEditorControls() ).thenReturn( asList( editorControl ) );
    return result;
  }
}