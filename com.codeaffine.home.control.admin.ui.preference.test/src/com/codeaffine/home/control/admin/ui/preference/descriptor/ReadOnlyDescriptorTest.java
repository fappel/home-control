package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.AttributePropertyDescriptorHelper.hasExactlyDelegateInstanceOf;
import static com.codeaffine.home.control.admin.ui.preference.descriptor.Messages.ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.util.viewer.property.PropertyDescriptor;

public class ReadOnlyDescriptorTest {

  private ReadOnlyDescriptor descriptor;

  @Before
  public void setUp() {
    descriptor = new ReadOnlyDescriptor( stubAttributeInfo( Object.class ) );
  }

  @Test
  public void createPropertyDescriptor() {
    IPropertyDescriptor actual = descriptor.createPropertyDescriptor();

    assertThat( actual )
      .matches( descriptor -> hasExactlyDelegateInstanceOf( descriptor, PropertyDescriptor.class ) )
      .matches( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) )
      .matches( descriptor -> descriptor.getId().equals( ATTRIBUTE_NAME ) );
  }

  @Test
  public void convertToRepresentationValue() {
    Object attributeValue = new Object();

    Object actual = descriptor.convertToRepresentationValue( attributeValue );

    assertThat( actual ).isEqualTo( attributeValue.toString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToRepresentationValueWithNullAsAttributeValueArgument() {
    descriptor.convertToRepresentationValue( null );
  }

  @Test
  public void convertToValue() {
    String attributeValueLabel = new Object().toString();

    Throwable actual = thrownBy( () -> descriptor.convertToValue( attributeValueLabel ) );

    assertThat( actual )
      .isInstanceOf( UnsupportedOperationException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION, attributeValueLabel, Object.class.getName() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToValueWithNullAsAttributeValueLabelArgument() {
    descriptor.convertToValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributorDescriptorArgument() {
    new ReadOnlyDescriptor( null );
  }
}