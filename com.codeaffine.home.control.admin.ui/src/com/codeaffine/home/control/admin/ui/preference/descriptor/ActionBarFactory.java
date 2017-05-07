package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
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

  void create( CellEditor cellEditor, List<AttributeAction> actions ) {
    verifyNotNull( cellEditor, "cellEditor" );
    verifyNotNull( actions, "actions" );

    Control control = cellEditor.getControl();
    List<Button> buttons = createActionButtons( actions, control );
    wireButtonsToControlEvents( control, buttons );
  }

  private static List<Button> createActionButtons( List<AttributeAction> actions, Control control ) {
    List<Button> result = createButtons( actions, control );
    wireButtonInstanceRelatedEvents( control, result );
    return result;
  }

  private static List<Button> createButtons( List<AttributeAction> actions, Control control ) {
    return actions
      .stream()
      .map( action -> createButton( control.getParent(), action ) )
      .collect( toList() );
  }

  private static Button createButton( Composite parent, AttributeAction action ) {
    ActionPresentation presentation = action.getPresentation( ActionPresentation.class );
    Button result = new Button( parent, presentation.getStyle() );
    result.setData( RWT.CUSTOM_VARIANT, "attributeCellEditorActionBar" );
    result.setText( presentation.getLabel() );
    result.addListener( SWT.Selection, evt -> action.run() );
    return result;
  }

  private static void wireButtonInstanceRelatedEvents( Control control, List<Button> buttons ) {
    buttons.forEach( button -> {
      control.addListener( SWT.Dispose, evt -> button.dispose() );
      button.addListener( SWT.Selection, evt -> buttons.forEach( actionButton -> actionButton.setVisible( false ) ) );
      button.addListener( SWT.FocusOut, evt -> buttons.forEach( actionButton -> actionButton.setVisible( false ) ) );
    } );
  }

  private static void wireButtonsToControlEvents( Control control, List<Button> buttons ) {
    control.addListener( SWT.Resize, initializeResizeObserver( control, buttons ).get() );
    control.addListener( SWT.Show, evt -> buttons.forEach( button -> button.setVisible( true ) ) );
    control.addListener( SWT.Hide, evt -> adjustButtonVisibiltyOnEditorControlHide( buttons ) );
  }

  private static AtomicReference<Listener> initializeResizeObserver( Control control, List<Button> buttons ) {
    AtomicReference<Listener> result = new AtomicReference<>();
    result.set( evt -> handleResize( control, buttons, result ) );
    return result;
  }

  private static void handleResize( Control control, List<Button> buttons, AtomicReference<Listener> resizeObserver ) {
    control.removeListener( SWT.Resize, resizeObserver.get() );
    layoutEditorCell( control, buttons );
    control.addListener( SWT.Resize, resizeObserver.get() );
  }

  private static void layoutEditorCell( Control editorControl, List<Button> actionButtons ) {
    Rectangle cellBounds = editorControl.getBounds();
    range( 0, actionButtons.size() ).forEach( i -> layoutActionButton( i, actionButtons, cellBounds ) );
    editorControl.setBounds( computeEditorBounds( cellBounds, actionButtons.size() ) );
  }

  private static Rectangle computeEditorBounds( Rectangle bounds, int buttonCount ) {
    return new Rectangle( bounds.x, bounds.y, bounds.width - bounds.height * buttonCount, bounds.height );
  }

  private static void layoutActionButton( int buttonIndex, List<Button> actionButtons, Rectangle cellBounds ) {
    Button button = actionButtons.get( buttonIndex );
    int xPos = computeButtonX( buttonIndex, actionButtons.size(), cellBounds );
    button.setBounds( xPos, cellBounds.y, cellBounds.height, cellBounds.height );
  }

  private static int computeButtonX( int buttonIndex, int buttonCount, Rectangle cellBounds ) {
    return cellBounds.x + cellBounds.width - cellBounds.height * ( buttonCount - buttonIndex );
  }

  private static void adjustButtonVisibiltyOnEditorControlHide( List<Button> buttons ) {
    boolean keepVisible = buttons.stream().anyMatch( button -> button.isFocusControl() );
    buttons.forEach( button -> button.setVisible( keepVisible ) );
  }
}