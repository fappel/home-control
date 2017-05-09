package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.Theme.*;
import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.*;
import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.CellEditorControlUtil.*;
import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.Messages.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.eclipse.rap.rwt.RWT.CUSTOM_VARIANT;
import static org.eclipse.swt.SWT.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptor;
import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptorSupplier;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

class ComponentFactory {

  private final CollectionValue collectionValue;

  ComponentFactory( CollectionValue collectionValue ) {
    this.collectionValue = collectionValue;
  }

  Collection<String> createAdditionalInfoKeys() {
    if( getGenericTypeParameters().size() == 2 ) {
      return asList( ADDITION_INFO_KEY, ADDITION_INFO_VALUE );
    }
    return asList( ADDITION_INFO_VALUE );
  }

  Shell createShell( Shell parent, Layout shellLayout ) {
    Shell result = new Shell( parent, TITLE | BORDER | APPLICATION_MODAL | RESIZE );
    result.setText( format( ADD_DIALOG_TITLE, getAttributeDisplayName() ) );
    result.setLayout( shellLayout );
    return result;
  }

  Label createDialogDescription( Composite parent ) {
    Label result = new Label( parent, NONE );
    result.setText( ADD_DIALOG_DESCRIPTION );
    return result;
  }

  Label createValidationMessageLabel( Composite parent ) {
    Label result = new Label( parent, WRAP );
    result.setText( "" );
    result.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_ADD_ELEMENT_DIALOG_VALIDATION_MESSAGE );
    return result;
  }

  Composite createElementGroup( Composite parent, Layout elementGroupLayout ) {
    Composite result = new Composite( parent, NONE );
    result.setLayout( elementGroupLayout );
    return result;
  }

  Composite createButtonGroup( Composite parent, Layout buttonGroupLayout ) {
    Composite result = new Composite( parent, NONE );
    result.setLayout( buttonGroupLayout );
    return result;
  }

  Button createCancelButton( Composite parent ) {
    Button result = new Button( parent, PUSH );
    result.setText( ADD_DIALOG_BUTTON_CANCEL );
    return result;
  }

  Button createOkButton( Composite parent ) {
    Button result = new Button( parent, PUSH );
    result.setText( ADD_DIALOG_BUTTON_OK );
    return result;
  }

  List<Control> createElementGroupContent( Composite parent ) {
    Queue<String> elementKeyParts = getElementKeyParts();
    return getGenericTypeParameters()
      .stream()
      .map( elementType -> createElementPartSection( parent, elementKeyParts, elementType ) )
      .collect( toList() );
  }

  private static Control createElementPartSection( Composite parent, Queue<String> keys, Class<?> elementType ) {
    String elementPartKey = keys.poll();
    createElementPartLabel( parent, elementPartKey );
    return createElementPartEditorControl( parent, elementPartKey, elementType );
  }

  private static Label createElementPartLabel( Composite parent, String elementPartLabel ) {
    Label result = new Label( parent, NONE );
    result.setText( elementPartLabel + ":");
    return result;
  }

  private static Control createElementPartEditorControl(
    Composite parent, String elementPartKey, Class<?> elementType )
  {
    ObjectInfo addElementInfo = new AddElementObjectInfo( elementType );
    AttributeDescriptorSupplier descriptorSupplier = new AttributeDescriptorSupplier( addElementInfo );
    AttributeDescriptor descriptor = descriptorSupplier.getDescriptor( elementPartKey );
    IPropertyDescriptor propertyDescriptor = descriptor.createPropertyDescriptor();
    CellEditor editor = propertyDescriptor.createPropertyEditor( parent );
    Control result = editor.getControl();
    deactivateEventHandler( result, FocusOut, DefaultSelection, KeyDown, Traverse );
    result.setVisible( true );
    result.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_ADD_ELEMENT_DIALOG_VALUE_EDITOR );
    setElementPartKey( result, elementPartKey );
    setCellEditor( result, editor );
    setAttributeDescriptor( result, descriptor );
    return result;
  }

  private String getAttributeDisplayName() {
    return collectionValue.getAttributeInfo().getDisplayName();
  }

  private Queue<String> getElementKeyParts() {
    return new LinkedList<>( createAdditionalInfoKeys() );
  }

  private List<Class<?>> getGenericTypeParameters() {
    return collectionValue.getAttributeInfo().getGenericTypeParametersOfAttributeType();
  }
}