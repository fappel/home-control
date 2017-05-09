package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.ADDITION_INFO_VALUE;
import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.AddElementDialogUtil.*;
import static com.codeaffine.home.control.admin.ui.test.DisplayHelper.flushPendingEvents;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.swt.SWT.OK;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;
import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class AddElementDialogTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private CollectionValue collectionValue;
  private Shell parentShell;

  @Before
  public void setUp() {
    parentShell = createAndOpenParentShell();
    AttributeInfo attributeInfo = stubAttributeInfo( asList( String.class ) );
    ObjectInfo objectInfo = mock( ObjectInfo.class );
    collectionValue = new CollectionValue( objectInfo, attributeInfo, new ArrayList<>() );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void open() {
    AddElementDialog.open( parentShell, collectionValue, mock( Consumer.class ) );
    flushPendingEvents();

    assertThat( displayHelper.getNewShells() ).hasSize( 2 );
  }

  @Test
  public void openDialog() {
    AddElementDialog dialog = new AddElementDialog( parentShell, new ComponentFactory( collectionValue ) );
    dialog.open( mock( DialogCallback.class ) );

    assertThat( dialog.getOkButton().isEnabled() ).isFalse();
    assertThat( dialog.getElementEditorControls() )
      .allMatch( control -> control.isFocusControl() )
      .allMatch( control -> control instanceof Text );
    assertThat( getShellSize( dialog ) )
      .isEqualTo( computeShellSize( Display.getCurrent().getBounds(), getShellSize( dialog ) ) );
    assertThat( dialog.getOkButton().getShell().getLocation() )
      .isEqualTo( computeShellLocation( Display.getCurrent().getBounds(), getShellSize( dialog ) ) );
  }

  @Test
  public void close() {
    AddElementDialog dialog = new AddElementDialog( parentShell, new ComponentFactory( collectionValue ) );
    DialogCallback callback = mock( DialogCallback.class );
    dialog.open( callback );

    dialog.close( OK );

    verify( callback ).dialogClosed( OK );
  }

  @Test
  public void putAdditionInfoEntry() {
    AddElementDialog dialog = new AddElementDialog( parentShell, new ComponentFactory( collectionValue ) );
    Object expected = "value";

    dialog.putAdditionInfoEntry( ADDITION_INFO_VALUE, expected );
    Map<String, Object> actual = dialog.getAdditionInfo();

    assertThat( actual )
      .containsKey( ADDITION_INFO_VALUE )
      .containsValue( expected );
  }

  @Test
  public void updateControlEnablement() {
    AddElementDialog dialog = new AddElementDialog( parentShell, new ComponentFactory( collectionValue ) );
    dialog.open( mock( DialogCallback.class ) );

    dialog.putAdditionInfoEntry( ADDITION_INFO_VALUE, "value" );
    dialog.updateControlEnablement();

    assertThat( dialog.getOkButton().isEnabled() ).isTrue();
  }

  @Test( expected = IllegalArgumentException.class )
  @SuppressWarnings( "unchecked" )
  public void openWithNullAsParentShellArgument() {
    AddElementDialog.open( null, collectionValue, mock( Consumer.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  @SuppressWarnings( "unchecked" )
  public void openWithNullAsCollectionValueArgument() {
    AddElementDialog.open( parentShell, null, mock( Consumer.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void openWithNullAsCallbackArgument() {
    AddElementDialog.open( null, collectionValue, null );
  }

  private Shell createAndOpenParentShell() {
    Shell result = displayHelper.createShell();
    result.open();
    return result;
  }

  private static AttributeInfo stubAttributeInfo( List<Class<?>> genericTypeInfos ) {
    AttributeInfo result = mock( AttributeInfo.class );
    when( result.getGenericTypeParametersOfAttributeType() ).thenReturn( genericTypeInfos );
    return result;
  }

  private static Point getShellSize( AddElementDialog dialog ) {
    return dialog.getOkButton().getShell().getSize();
  }
}