package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.preference.attribute.ComboBoxPropertyDescriptorHelper.getSelectionValues;
import static com.codeaffine.home.control.admin.ui.test.AttributeDescriptorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;

public class BooleanAdapterTest {

  private BooleanAdapter adapter;

  @Before
  public void setUp() {
    adapter = new BooleanAdapter( stubAttributeDescriptor() );
  }

  @Test
  public void createPropertyDescriptor() {
    PropertyDescriptor actual = adapter.createPropertyDescriptor();

    assertThat( actual )
      .matches( descriptor -> getSelectionValues( descriptor ).equals( BooleanAdapter.SELECTION_LABELS ) )
      .matches( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) )
      .matches( descriptor -> descriptor.getId().equals( ATTRIBUTE_NAME ) );
  }

  @Test
  public void convertToLabelOfTrue() {
    Object actual = adapter.convertToLabel( Boolean.TRUE );

    assertThat( actual ).isEqualTo( BooleanAdapter.LABEL_INDEX_TRUE );
  }

  @Test
  public void convertToLabelOfFalse() {
    Object actual = adapter.convertToLabel( Boolean.FALSE );

    assertThat( actual ).isEqualTo( BooleanAdapter.LABEL_INDEX_FALSE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToLabelWithNullAsAttributeValueArgument() {
    adapter.convertToLabel( null );
  }

  @Test
  public void convertToAttributeValueOfFalseSelection() {
    Object actual = adapter.convertToAttributeValue( Integer.valueOf( BooleanAdapter.LABEL_INDEX_FALSE ) );

    assertThat( actual ).isSameAs( Boolean.FALSE );
  }

  @Test
  public void convertToAttributeValueOfTrueSelection() {
    Object actual = adapter.convertToAttributeValue( Integer.valueOf( BooleanAdapter.LABEL_INDEX_TRUE ) );

    assertThat( actual ).isSameAs( Boolean.TRUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToAttributeWithNullAsAttributeValueLabelArgument() {
    adapter.convertToAttributeValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new BooleanAdapter( null );
  }
}