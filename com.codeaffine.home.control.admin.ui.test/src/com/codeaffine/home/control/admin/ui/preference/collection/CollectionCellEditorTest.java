package com.codeaffine.home.control.admin.ui.preference.collection;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class CollectionCellEditorTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private CollectionCellEditor editor;
  private Shell parent;

  @Before
  public void setUp() {
    parent = displayHelper.createShell();
    parent.open();
    editor = new CollectionCellEditor( parent );
  }

  @Test
  public void createControlByConstructor() {
    assertThat( parent.getChildren() )
      .hasSize( 1 )
      .allMatch( control -> control instanceof Label );
  }

  @Test
  public void doSetValue() {
    Object expected = new Object();

    editor.doSetValue( expected );
    Object actual = editor.doGetValue();

    assertThat( actual ).isSameAs( expected );
    assertThat( ( ( Label )editor.getControl() ).getText() ).isEmpty();
  }

  @Test
  public void doGetValueAfterConstruction() {
    Object actual = editor.doGetValue();

    assertThat( actual ).isNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsParentArgument() {
    new CollectionCellEditor( null );
  }
}