package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.*;
import static com.codeaffine.home.control.admin.ui.test.SWTEventHelper.trigger;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class ActionBarFactoryTest {

  private static final int ADD_ACTION_INDEX = 0;

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private List<AttributeAction> actions;
  private TextCellEditor cellEditor;
  private ActionBarFactory factory;
  private Shell cellEditorParent;
  private Runnable action;

  @Before
  public void setUp() {
    factory = new ActionBarFactory();
    cellEditorParent = createAndOpenShell();
    cellEditor = new TextCellEditor( cellEditorParent );
    action = mock( Runnable.class );
    actions = new ArrayList<>( asList( new AttributeAction( action, ADD ) ) );
  }

  @Test
  public void create() {
    factory.create( cellEditor, actions );

    assertThat( collectButtons( cellEditorParent ) )
      .hasSize( actions.size() )
      .allMatch( button -> button.getText().equals( ADD.getLabel() ) )
      .allMatch( button -> ( button.getStyle() & ADD.getStyle() ) > ADD_ACTION_INDEX );
  }

  @Test
  public void createAndShowEditorControl() {
    factory.create( cellEditor, actions );
    trigger( SWT.Show ).on( cellEditor.getControl() );

    assertThat( collectButtons( cellEditorParent ) )
      .hasSize( actions.size() )
      .allMatch( button -> button.isVisible() );
  }

  @Test
  public void createShowAndHideEditorControl() {
    factory.create( cellEditor, actions );
    trigger( SWT.Show ).on( cellEditor.getControl() );
    trigger( SWT.Hide ).on( cellEditor.getControl() );

    assertThat( collectButtons( cellEditorParent ) )
      .hasSize( actions.size() )
      .allMatch( button -> !button.isVisible() );
  }

  @Test
  public void createShowAndSetFocusToActionButton() {
    factory.create( cellEditor, actions );
    trigger( SWT.Show ).on( cellEditor.getControl() );
    trigger( SWT.FocusIn ).on( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX ) );

    assertThat( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX ).isVisible() ).isTrue();
    assertThat( cellEditor.getControl().isVisible() ).isFalse();
  }

  @Test
  public void createShowSetFocusAndRemoveFocusOnActionButton() {
    factory.create( cellEditor, actions );
    trigger( SWT.Show ).on( cellEditor.getControl() );
    trigger( SWT.FocusIn ).on( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX ) );
    trigger( SWT.FocusOut ).on( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX ) );

    assertThat( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX ).isVisible() ).isFalse();
    assertThat( cellEditor.getControl().isVisible() ).isFalse();
  }

  @Test
  public void createAndTriggerActions() {
    factory.create( cellEditor, actions );
    trigger( SWT.Show ).on( cellEditor.getControl() );
    trigger( SWT.Selection ).on( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX ) );

    verify( action ).run();
    assertThat( collectButtons( cellEditorParent ) ).allMatch( button -> !button.isVisible() );
  }

  @Test
  public void createAndResizeCellEditorControl() {
    actions.add( new AttributeAction( () -> {}, DELETE ) );
    factory.create( cellEditor, actions );
    cellEditor.getControl().setLocation( 5, 6 );

    cellEditor.getControl().setSize( 60, 10 ); // not clear why call needs to be done twice, but works at runtime. Hm..
    cellEditor.getControl().setSize( 60, 10 );

    assertThat( cellEditor.getControl().getBounds() )
      .isEqualTo( new Rectangle( 5, 6, 40, 10 ) );
    assertThat( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX ).getBounds() )
      .isEqualTo( new Rectangle( 45, 6, 10, 10 ) );
    assertThat( collectButtons( cellEditorParent ).get( ADD_ACTION_INDEX + 1 ).getBounds() )
      .isEqualTo( new Rectangle( 55, 6, 10, 10 ) );
  }

  @Test
  public void createAndDisposeCellEditor() {
    factory.create( cellEditor, actions );
    cellEditor.getControl().dispose();

    assertThat( collectButtons( cellEditorParent ) ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsCellEditorArgument() {
    factory.create( null, actions );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsActionsArgument() {
    factory.create( cellEditor, null );
  }

  private static List<Button> collectButtons( Composite cellEditorParent ) {
    return Stream.of( cellEditorParent.getChildren() )
      .filter( control -> control instanceof Button )
      .map( control -> ( Button )control )
      .collect( toList() );
  }

  private Shell createAndOpenShell() {
    Shell result = displayHelper.createShell();
    result.open();
    return result;
  }
}