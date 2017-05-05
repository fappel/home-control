package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeActionType;

class AttributeCellEditorActionBarFactory {

  void create( Composite parent, CellEditor cellEditor, Set<AttributeAction> actions ) {
    Control control = cellEditor.getControl();

    List<Button> buttons = actions
      .stream()
      .sorted( ( action1, action2 ) -> action1.getType().compareTo( action2.getType() ) )
      .map( action -> createButton( parent, action ) )
      .collect( toList() );
    buttons.forEach( button -> {
      control.addListener( SWT.Dispose, evt -> button.dispose() );
      button.addListener( SWT.Selection, evt -> buttons.forEach( actionButton -> actionButton.setVisible( false ) ) );
      button.addListener( SWT.FocusOut, evt -> buttons.forEach( actionButton -> actionButton.setVisible( false ) ) );
    } );

    AtomicReference<Listener> boundsComputer = new AtomicReference<>();
    boundsComputer.set( evt -> {
      control.removeListener( SWT.Resize, boundsComputer.get() );
      Rectangle controlBounds = control.getBounds();
      for( int i = 0; i < buttons.size(); i++ ) {
        Button button = buttons.get( i );
        button.setBounds( controlBounds.x + controlBounds.width - controlBounds.height * ( buttons.size() - i ),
                          controlBounds.y,
                          controlBounds.height,
                          controlBounds.height );
      }
      buttons.forEach( button -> button.moveAbove( control ) );
      control.setBounds( controlBounds.x,
                         controlBounds.y,
                         controlBounds.width - controlBounds.height * buttons.size(),
                         controlBounds.height );
      control.addListener( SWT.Resize, boundsComputer.get() );
    } );

    control.addListener( SWT.Resize, boundsComputer.get() );
    control.addListener( SWT.Show, evt -> buttons.forEach( button -> button.setVisible( true ) ) );
    control.addListener( SWT.Hide, evt -> {
      boolean keepVisible = buttons.stream().anyMatch( button -> button.isFocusControl() );
      buttons.forEach( button -> button.setVisible( keepVisible ) );
    } );
  }

  private static Button createButton( Composite parent, AttributeAction action ) {
    Button result = new Button( parent, getStyle( action.getType() ) );
    result.setData( RWT.CUSTOM_VARIANT, "attributeCellEditorActionBar" );
    result.setText( getLabel( action.getType() ) );
    result.addListener( SWT.Selection, evt -> action.run() );
    return result;
  }

  private static int getStyle( AttributeActionType type ) {
    switch( type ) {
      case ADD:
      case DELETE:
        return SWT.PUSH;
      case UP:
        return SWT.ARROW | SWT.UP;
      case DOWN:
        return SWT.ARROW | SWT.DOWN;
    }
    throw new IllegalStateException( "Unknow AttributeActionType " + type );
  }

  private static String getLabel( AttributeActionType type ) {
    switch( type ) {
      case ADD:
        return "+";
      case DELETE:
        return "-";
      case UP:
      case DOWN:
        return "";
    }
    throw new IllegalStateException( "Unknow AttributeActionType " + type );
  }
}