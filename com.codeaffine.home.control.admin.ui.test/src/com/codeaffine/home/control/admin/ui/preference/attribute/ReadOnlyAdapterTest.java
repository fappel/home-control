package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.preference.attribute.Messages.ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION;
import static com.codeaffine.home.control.admin.ui.test.AttributeDescriptorHelper.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;

public class ReadOnlyAdapterTest {

  private ReadOnlyAdapter adapter;

  @Before
  public void setUp() {
    adapter = new ReadOnlyAdapter( stubAttributeDescriptor( Object.class ) );
  }

  @Test
  public void createPropertyDescriptor() {
    PropertyDescriptor actual = adapter.createPropertyDescriptor();

    assertThat( actual )
      .isExactlyInstanceOf( PropertyDescriptor.class )
      .matches( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) )
      .matches( descriptor -> descriptor.getId().equals( ATTRIBUTE_NAME ) );
  }

  @Test
  public void convertToLabel() {
    Object attributeValue = new Object();

    Object actual = adapter.convertToLabel( attributeValue );

    assertThat( actual ).isEqualTo( attributeValue.toString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToLabelWithNullAsAttributeValueArgument() {
    adapter.convertToLabel( null );
  }

  @Test
  public void convertToAttributeValue() {
    String attributeValueLabel = new Object().toString();

    Throwable actual = thrownBy( () -> adapter.convertToAttributeValue( attributeValueLabel ) );

    assertThat( actual )
      .isInstanceOf( UnsupportedOperationException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION, attributeValueLabel, Object.class.getName() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToAttributeValueWithNullAsAttributeValueLabelArgument() {
    adapter.convertToAttributeValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributorDescriptorArgument() {
    new ReadOnlyAdapter( null );
  }
}