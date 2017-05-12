package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.AttributePropertyDescriptorHelper.hasExactlyDelegateInstanceOf;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.util.viewer.property.TextPropertyDescriptor;

public class TextDescriptorTest {

  private static final String ATTRIBUTE_VALUE = "VALUE";

  private TextDescriptor descriptor;

  @Before
  public void setUp() {
    descriptor = new TextDescriptor( stubAttributeInfo() );
  }

  @Test
  public void createPropertyDescriptor() {
    IPropertyDescriptor actual = descriptor.createPropertyDescriptor();

    assertThat( actual )
      .matches( descriptor -> hasExactlyDelegateInstanceOf( descriptor, TextPropertyDescriptor.class ) )
      .matches( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) )
      .matches( descriptor -> descriptor.getId().equals( ATTRIBUTE_NAME ) );
  }

  @Test
    public void convertToRepresentationValue() {
    Object actual = descriptor.convertToRepresentationValue( ATTRIBUTE_VALUE );

    assertThat( actual ).isEqualTo( ATTRIBUTE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToRepresentationValueWithNullAsAttributeValueArgument() {
    descriptor.convertToRepresentationValue( null );
  }

  @Test
  public void convertToValue() {
    Object actual = descriptor.convertToValue( ATTRIBUTE_VALUE.toString() );

    assertThat( actual ).isEqualTo( ATTRIBUTE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToValueWithNullAsAttributeValueArgument() {
    descriptor.convertToValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new TextDescriptor( null );
  }
}