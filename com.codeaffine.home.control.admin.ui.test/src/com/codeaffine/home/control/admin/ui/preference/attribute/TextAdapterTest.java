package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.test.AttributeDescriptorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.TextPropertyDescriptor;

public class TextAdapterTest {

  private static final String ATTRIBUTE_VALUE = "VALUE";

  private TextAdapter adapter;

  @Before
  public void setUp() {
    adapter = new TextAdapter( stubAttributeDescriptor() );
  }

  @Test
  public void createPropertyDescriptor() {
    PropertyDescriptor actual = adapter.createPropertyDescriptor();

    assertThat( actual )
      .isInstanceOf( TextPropertyDescriptor.class )
      .matches( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) )
      .matches( descriptor -> descriptor.getId().equals( ATTRIBUTE_NAME ) );
  }

  @Test
  public void convertToLabel() {
    Object actual = adapter.convertToLabel( ATTRIBUTE_VALUE );

    assertThat( actual ).isEqualTo( ATTRIBUTE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToLabelWithNullAsAttributeValueArgument() {
    adapter.convertToLabel( null );
  }

  @Test
  public void convertToAttributeValue() {
    Object actual = adapter.convertToAttributeValue( ATTRIBUTE_VALUE.toString() );

    assertThat( actual ).isEqualTo( ATTRIBUTE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToAttributeValueWithNullAsAttributeValueArgument() {
    adapter.convertToAttributeValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new TextAdapter( null );
  }
}