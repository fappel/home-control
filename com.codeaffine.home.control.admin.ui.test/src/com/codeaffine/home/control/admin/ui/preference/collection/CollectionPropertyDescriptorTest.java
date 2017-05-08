package com.codeaffine.home.control.admin.ui.preference.collection;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class CollectionPropertyDescriptorTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private CollectionPropertyDescriptor descriptor;

  @Before
  public void setUp() {
    descriptor = new CollectionPropertyDescriptor( "id", "displayName" );
  }

  @Test
  public void createPropertyEditor() {
    CellEditor actual = descriptor.createPropertyEditor( displayHelper.createShell() );

    assertThat( actual ).isInstanceOf( CollectionCellEditor.class );
  }

  @Test
  public void getLabelProvider() {
    ILabelProvider labelProvider = descriptor.getLabelProvider();
    String actual = labelProvider.getText( "text" );

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsIdArgument() {
    new CollectionPropertyDescriptor( null, "displayName" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDisplayNameArgument() {
    new CollectionPropertyDescriptor( "id", null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createPropertyEditorWithNullAsParentArgument() {
    descriptor.createPropertyEditor( null );
  }
}