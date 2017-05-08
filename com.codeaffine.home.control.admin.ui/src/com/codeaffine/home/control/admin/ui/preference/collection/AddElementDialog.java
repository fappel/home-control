package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.internal.util.FormDatas.attach;
import static com.codeaffine.home.control.admin.ui.preference.collection.Messages.*;
import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.Math.max;
import static java.lang.String.format;
import static java.util.Arrays.*;
import static org.eclipse.jface.layout.GridLayoutFactory.fillDefaults;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptor;
import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptorSupplier;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

class AddElementDialog extends Dialog {

  private static final int MARGIN = 20;

  private final Map<String, Object> additionInfo;
  private final CollectionValue collectionValue;

  private Button ok;

  private AddElementDialog( Shell parent, CollectionValue collectionValue ) {
    super( parent );
    this.collectionValue = collectionValue;
    this.additionInfo = new HashMap<>();
  }

  static void open( Shell parent, CollectionValue collectionValue, Consumer<Map<String, Object>> additionCallback ) {
    verifyNotNull( additionCallback, "additionCallback" );
    verifyNotNull( collectionValue, "collectionValue" );
    verifyNotNull( parent, "parent" );

    AddElementDialog dialog = new AddElementDialog( parent, collectionValue );
    parent.getDisplay().asyncExec( () -> {
      dialog.open( returnCode -> {
        if( returnCode == SWT.OK ) {
          additionCallback.accept( dialog.getAdditionInfo() );
        }
      } );
    } );
  }

  @Override
  protected void prepareOpen() {
    shell = new Shell( getParent(), SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE );
    shell.setText( format( ADD_DIALOG_TITLE, collectionValue.getAttributeInfo().getDisplayName() ) );

    shell.setLayout( new FormLayout() );

    Label description = new Label( shell, SWT.NONE );
    description.setText( ADD_DIALOG_DESCRIPTION );
    attach( description ).toLeft( MARGIN ).toTop( MARGIN ).toRight( MARGIN );

    Label validationMessage = new Label( shell, SWT.NONE );
    validationMessage.setText( "" );
    attach( validationMessage ).toLeft( MARGIN ).atTopTo( description, MARGIN ).toRight( MARGIN );
    validationMessage.setData( RWT.CUSTOM_VARIANT, "addElementDialogValidationMessage" );

    Composite elementGroup = new Composite( shell, SWT.NONE );
    attach( elementGroup ).toLeft( MARGIN ).atTopTo( validationMessage, MARGIN ).toRight( MARGIN );
    elementGroup.setLayout( fillDefaults().numColumns( 2 ).create() );

    List<Class<?>> genericTypeParameters = collectionValue.getAttributeInfo().getGenericTypeParametersOfAttributeType();
    Queue<String> labels = genericTypeParameters.size() == 2 ? new LinkedList<>( asList( ADDITION_INFO_KEY, ADDITION_INFO_VALUE ) ) : new LinkedList<>( asList( ADDITION_INFO_VALUE ) );
    additionInfo.clear();
    labels.forEach( label -> additionInfo.put( label, null ) );

    genericTypeParameters.forEach( elementType -> {
      String elementPartType = labels.poll();

      Label label = new Label( elementGroup, SWT.WRAP );
      label.setText( elementPartType + ":");
      label.addListener( SWT.KeyDown, evt -> closeShellOnEscapeKey( evt ) );

      ObjectInfo addElementInfo = new AddElementObjectInfo( elementType );
      AttributeDescriptor descriptor = new AttributeDescriptorSupplier( addElementInfo ).getDescriptor( "attributeName" );
      IPropertyDescriptor propertyDescriptor = descriptor.createPropertyDescriptor();
      CellEditor editor = propertyDescriptor.createPropertyEditor( elementGroup );
      deactivateEvents( editor, SWT.FocusOut, SWT.DefaultSelection, SWT.KeyDown, SWT.Traverse );
      editor.getControl().setVisible( true );
      GridDataFactory.fillDefaults().grab( true, false ).applyTo( editor.getControl() );
      editor.getControl().addListener( SWT.KeyDown, evt -> closeShellOnEscapeKey( evt ) );
      editor.getControl().setData( RWT.CUSTOM_VARIANT, "addElementDialogValueEditor" );
      editor.getControl().addListener( SWT.Modify, evt -> {
        shell.getDisplay().asyncExec( () -> {
          if( editor.getErrorMessage() == null ) {
            validationMessage.setText( "" );
            additionInfo.put( elementPartType, descriptor.convertToValue( editor.getValue() ) );
          } else {
            validationMessage.setText( editor.getErrorMessage() );
            additionInfo.put( elementPartType, null );
          }
          updateOkEnablement( additionInfo );
        } );
      } );
    } );
    elementGroup.getChildren()[ 1 ].setFocus();

    Composite buttonGroup = new Composite( shell, SWT.NONE );
    attach( buttonGroup ).atTopTo( elementGroup, MARGIN ).toRight( MARGIN ).toBottom( MARGIN );
    buttonGroup.setLayout( fillDefaults().numColumns( 2 ).equalWidth( true ).create() );

    Button cancel = new Button( buttonGroup, SWT.PUSH );
    cancel.setText( "Cancel" );
    cancel.addListener( SWT.Selection, evt -> shell.close() );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( cancel );

    ok = new Button( buttonGroup, SWT.PUSH );
    ok.setText( "OK" );
    ok.addListener( SWT.Selection, evt -> handleOkPressed() );
    ok.addListener( SWT.DefaultSelection, evt -> handleOkPressed() );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( ok );
    updateOkEnablement( additionInfo );
    shell.setDefaultButton( ok );

    shell.pack();
    Rectangle displayBounds = shell.getDisplay().getBounds();
    Rectangle shellBounds = shell.getBounds();
    shell.setSize( max( shellBounds.width, displayBounds.width / 2 ), shellBounds.height );
    shell.setLocation( new Point( ( displayBounds.width - shell.getSize().x ) / 2, ( displayBounds.height - shell.getSize().y ) / 2 ) );
  }

  private Map<String, Object> getAdditionInfo() {
    return additionInfo;
  }

  private void handleOkPressed() {
    returnCode = SWT.OK;
    shell.close();
  }

  private void updateOkEnablement( Map<String, Object> elementValueDefinition ) {
    ok.setEnabled( elementValueDefinition.values().stream().allMatch( value -> value != null ) );
  }

  private void closeShellOnEscapeKey( Event evt ) {
    if( evt.keyCode == 27 ) {
      shell.close();
    }
  }

  private static void deactivateEvents( CellEditor editor, int ...eventTypes ) {
    stream( eventTypes ).forEach( eventType -> {
      Listener[] listeners = editor.getControl().getListeners( eventType );
      Stream.of( listeners ).forEach( listener -> editor.getControl().removeListener( eventType, listener ) );
    } );
  }
}