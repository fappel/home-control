package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;

public class ActionBarAdapterTest {

  private ActionBarFactory actionBarFactory;
  private IPropertyDescriptor delegate;
  private ActionBarAdapter adapter;
  private AttributeInfo info;

  @Before
  public void setUp() {
    delegate = mock( IPropertyDescriptor.class );
    info = mock( AttributeInfo.class );
    actionBarFactory = mock( ActionBarFactory.class );
    adapter = new ActionBarAdapter( delegate, info, actionBarFactory );
  }

  @Test
  public void createPropertyEditor() {
    CellEditor expected = mock( CellEditor.class );
    Composite parent = mock( Composite.class );
    when( delegate.createPropertyEditor( parent ) ).thenReturn( expected );

    CellEditor actual = adapter.createPropertyEditor( parent );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void createPropertyEditorIfInfoHasActions() {
    CellEditor editor = mock( CellEditor.class );
    Composite parent = mock( Composite.class );
    List<AttributeAction> actions = asList( mock( AttributeAction.class ) );
    when( delegate.createPropertyEditor( parent ) ).thenReturn( editor );
    when( info.getActions() ).thenReturn( actions );

    CellEditor actual = adapter.createPropertyEditor( parent );

    verify( actionBarFactory ).create( editor, actions );
    assertThat( actual ).isSameAs( editor );
  }

  @Test
  public void createPropertyEditorIfInfoHasActionsButNoEditorIsCreated() {
    Composite parent = mock( Composite.class );
    List<AttributeAction> actions = asList( mock( AttributeAction.class ) );
    when( info.getActions() ).thenReturn( actions );

    CellEditor actual = adapter.createPropertyEditor( parent );

    verify( actionBarFactory, never() ).create( any(), any() );
    assertThat( actual ).isNull();
  }

  @Test
  public void getCategory() {
    String expected = "expected";
    when( delegate.getCategory() ).thenReturn( expected );

    String actual = adapter.getCategory();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getDescription() {
    String expected = "expected";
    when( delegate.getDescription() ).thenReturn( expected );

    String actual = adapter.getDescription();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getDisplayName() {
    String expected = "expected";
    when( delegate.getDisplayName() ).thenReturn( expected );

    String actual = adapter.getDisplayName();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getFilterFlags() {
    String[] expected = new String[] { "expected" };
    when( delegate.getFilterFlags() ).thenReturn( expected );

    String[] actual = adapter.getFilterFlags();

    assertThat( actual ).isEqualTo( expected );

  }

  @Test
  public void getHelpContextIds() {
    Object expected = new Object();
    when( delegate.getHelpContextIds() ).thenReturn( expected );

    Object actual = adapter.getHelpContextIds();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void getId() {
    Object expected = new Object();
    when( delegate.getId() ).thenReturn( expected );

    Object actual = adapter.getId();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void getLabelProvider() {
    ILabelProvider expected = new LabelProvider();
    when( delegate.getLabelProvider() ).thenReturn( expected );

    ILabelProvider actual = adapter.getLabelProvider();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void isCompatibleWith() {
    IPropertyDescriptor anotherDescriptor = mock( IPropertyDescriptor.class );
    when( delegate.isCompatibleWith( anotherDescriptor ) ).thenReturn( true );

    boolean actual = adapter.isCompatibleWith( anotherDescriptor );

    assertThat( actual ).isTrue();
  }

  @Test
  public void getDelegate() {
    IPropertyDescriptor actual = adapter.getDelegate();

    assertThat( actual ).isSameAs( delegate );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDelegateArgument() {
    new ActionBarAdapter( null, info, actionBarFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgument() {
    new ActionBarAdapter( delegate, null, actionBarFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsCellEditorActionBarFactoryArgument() {
    new ActionBarAdapter( delegate, info, null );
  }
}