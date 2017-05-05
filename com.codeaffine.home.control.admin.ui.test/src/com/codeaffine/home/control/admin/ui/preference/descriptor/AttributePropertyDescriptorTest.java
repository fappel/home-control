package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.newLinkedHashSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

public class AttributePropertyDescriptorTest {

  private AttributeCellEditorActionBarFactory actionBarFactory;
  private AttributePropertyDescriptor descriptor;
  private IPropertyDescriptor delegate;
  private AttributeInfo info;

  @Before
  public void setUp() {
    delegate = mock( IPropertyDescriptor.class );
    info = mock( AttributeInfo.class );
    actionBarFactory = mock( AttributeCellEditorActionBarFactory.class );
    descriptor = new AttributePropertyDescriptor( delegate, info, actionBarFactory );
  }

  @Test
  public void createPropertyEditor() {
    CellEditor expected = mock( CellEditor.class );
    Composite parent = mock( Composite.class );
    when( delegate.createPropertyEditor( parent ) ).thenReturn( expected );

    CellEditor actual = descriptor.createPropertyEditor( parent );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void createPropertyEditorIfInfoHasActions() {
    CellEditor editor = mock( CellEditor.class );
    Composite parent = mock( Composite.class );
    LinkedHashSet<AttributeAction> actions = newLinkedHashSet( mock( AttributeAction.class ) );
    when( delegate.createPropertyEditor( parent ) ).thenReturn( editor );
    when( info.getActions() ).thenReturn( actions );

    CellEditor actual = descriptor.createPropertyEditor( parent );

    verify( actionBarFactory ).create( parent, editor, actions );
    assertThat( actual ).isSameAs( editor );
  }

  @Test
  public void createPropertyEditorIfInfoHasActionsButNoEditorIsCreated() {
    Composite parent = mock( Composite.class );
    LinkedHashSet<AttributeAction> actions = newLinkedHashSet( mock( AttributeAction.class ) );
    when( info.getActions() ).thenReturn( actions );

    CellEditor actual = descriptor.createPropertyEditor( parent );

    verify( actionBarFactory, never() ).create( any(), any(), any() );
    assertThat( actual ).isNull();
  }

  @Test
  public void getCategory() {
    String expected = "expected";
    when( delegate.getCategory() ).thenReturn( expected );

    String actual = descriptor.getCategory();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getDescription() {
    String expected = "expected";
    when( delegate.getDescription() ).thenReturn( expected );

    String actual = descriptor.getDescription();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getDisplayName() {
    String expected = "expected";
    when( delegate.getDisplayName() ).thenReturn( expected );

    String actual = descriptor.getDisplayName();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getFilterFlags() {
    String[] expected = new String[] { "expected" };
    when( delegate.getFilterFlags() ).thenReturn( expected );

    String[] actual = descriptor.getFilterFlags();

    assertThat( actual ).isEqualTo( expected );

  }

  @Test
  public void getHelpContextIds() {
    Object expected = new Object();
    when( delegate.getHelpContextIds() ).thenReturn( expected );

    Object actual = descriptor.getHelpContextIds();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void getId() {
    Object expected = new Object();
    when( delegate.getId() ).thenReturn( expected );

    Object actual = descriptor.getId();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void getLabelProvider() {
    ILabelProvider expected = new LabelProvider();
    when( delegate.getLabelProvider() ).thenReturn( expected );

    ILabelProvider actual = descriptor.getLabelProvider();

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void isCompatibleWith() {
    IPropertyDescriptor anotherDescriptor = mock( IPropertyDescriptor.class );
    when( delegate.isCompatibleWith( anotherDescriptor ) ).thenReturn( true );

    boolean actual = descriptor.isCompatibleWith( anotherDescriptor );

    assertThat( actual ).isTrue();
  }

  @Test
  public void getDelegate() {
    IPropertyDescriptor actual = descriptor.getDelegate();

    assertThat( actual ).isSameAs( delegate );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDelegateArgument() {
    new AttributePropertyDescriptor( null, info, actionBarFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgument() {
    new AttributePropertyDescriptor( delegate, null, actionBarFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsCellEditorActionBarFactoryArgument() {
    new AttributePropertyDescriptor( delegate, info, null );
  }
}