package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.util.List;
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

class ActionBarFactory {

  void create( Composite parent, CellEditor cellEditor, List<AttributeAction> actions ) {
    Control control = cellEditor.getControl();

    List<Button> buttons = actions
      .stream()
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
      range( 0, buttons.size() ).forEach( i -> {
        Button button = buttons.get( i );
        button.setBounds( controlBounds.x + controlBounds.width - controlBounds.height * ( buttons.size() - i ),
                          controlBounds.y,
                          controlBounds.height,
                          controlBounds.height );
        button.moveAbove( control );
      } );
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
    ActionPresentation presentation = action.getPresentation( ActionPresentation.class );
    Button result = new Button( parent, presentation.getStyle() );
    result.setData( RWT.CUSTOM_VARIANT, "attributeCellEditorActionBar" );
    result.setText( presentation.getLabel() );
    result.addListener( SWT.Selection, evt -> action.run() );
    return result;
  }
}