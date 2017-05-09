package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static com.codeaffine.home.control.admin.ui.internal.util.FormDatas.attach;
import static com.codeaffine.home.control.admin.ui.preference.collection.dialog.AddElementDialogUtil.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.jface.layout.GridLayoutFactory.fillDefaults;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;

public class AddElementDialog extends Dialog {

  private static final int MARGIN = 20;

  private final EventHandlerRegistration eventHandlerRegistration;
  private final Map<String, Object> additionInfo;
  private final ComponentFactory factory;

  private List<Control> elementEditorControls;
  private Composite elementGroup;
  private Composite buttonGroup;
  private Label description;
  private Label validation;
  private Button cancel;
  private Button ok;

  AddElementDialog( Shell parent, ComponentFactory componentFactory ) {
    super( parent );
    this.eventHandlerRegistration = new EventHandlerRegistration( this );
    this.additionInfo = new HashMap<>();
    this.factory = componentFactory;
  }

  public static void open( Shell parent, CollectionValue collectionValue, Consumer<Map<String, Object>> callback ) {
    verifyNotNull( collectionValue, "collectionValue" );
    verifyNotNull( callback, "callback" );
    verifyNotNull( parent, "parent" );

    AddElementDialog dialog = new AddElementDialog( parent, new ComponentFactory( collectionValue ) );
    asyncExec( () -> dialog.open( returnCode -> triggerCallback( callback, dialog, returnCode )) );
  }

  @Override
  protected void prepareOpen() {
    prepareAdditionInfo();
    createControls();
    applyLayout();
    updateControlEnablement();
    registerEventHandlers();
    setFocus();
    adjustShellBounds();
  }

  Map<String, Object> getAdditionInfo() {
    return additionInfo;
  }

  void putAdditionInfoEntry( String elementPartKey, Object elementValuePart ) {
    additionInfo.put( elementPartKey, elementValuePart );
  }

  void setValidationText( String errorMessage ) {
    validation.setText( errorMessage );
  }

  void close( int returnCode ) {
    this.returnCode = returnCode;
    shell.close();
  }

  void updateControlEnablement() {
    ok.setEnabled( additionInfo.values().stream().allMatch( value -> value != null ) );
  }

  List<Control> getElementEditorControls() {
    return elementEditorControls;
  }

  Button getCancelButton() {
    return cancel;
  }

  Button getOkButton() {
    return ok;
  }

  private void prepareAdditionInfo() {
    Collection<String> keys = factory.createAdditionalInfoKeys();
    additionInfo.clear();
    keys.forEach( key -> additionInfo.put( key, null ) );
  }

  private void createControls() {
    shell = factory.createShell( this.getParent(), new FormLayout() );
    description = factory.createDialogDescription( shell );
    validation = factory.createValidationMessageLabel( shell );
    elementGroup = factory.createElementGroup( shell, fillDefaults().numColumns( 2 ).create() );
    elementEditorControls = factory.createElementGroupContent( elementGroup );
    buttonGroup = factory.createButtonGroup( shell, fillDefaults().numColumns( 2 ).equalWidth( true ).create() );
    cancel = factory.createCancelButton( buttonGroup );
    ok = factory.createOkButton( buttonGroup );
  }

  private void applyLayout() {
    attach( description ).toLeft( MARGIN ).toTop( MARGIN ).toRight( MARGIN );
    attach( validation ).toLeft( MARGIN ).atTopTo( description, MARGIN ).toRight( MARGIN );
    attach( elementGroup ).toLeft( MARGIN ).atTopTo( validation, MARGIN ).toRight( MARGIN );
    attach( buttonGroup ).atTopTo( elementGroup, MARGIN ).toRight( MARGIN ).toBottom( MARGIN );
    elementEditorControls.forEach( control -> GridDataFactory.fillDefaults().grab( true, false ).applyTo( control ) );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( cancel );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( ok );
  }

  private void registerEventHandlers() {
    eventHandlerRegistration.initialize();
    ok.getShell().setDefaultButton( ok );
  }

  private void setFocus() {
    elementEditorControls.get( 0 ).setFocus();
  }

  private void adjustShellBounds() {
    shell.pack();
    shell.setSize( computeShellSize( shell.getDisplay().getBounds(), shell.getBounds() ) );
    shell.setLocation( computeShellLocation( shell.getDisplay().getBounds(), shell.getSize() ) );
  }
}