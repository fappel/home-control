package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.AttributePropertyDescriptorHelper.*;
import static com.codeaffine.home.control.admin.ui.preference.descriptor.Messages.ERROR_CONVERSION_TO_ATTRIBUTE_VALUE;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.util.viewer.property.TextPropertyDescriptor;

public class StandardDescriptorTest {

  private static final String ATTRIBUTE_VALUE_LABEL = "2";
  private static final String NOT_A_VALID_INPUT = "not-a-valid-input";

  private StandardDescriptor descriptor;

  @Before
  public void setUp() {
    descriptor = new StandardDescriptor( stubAttributeInfo( Integer.class ) );
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
  public void validateValidInputString() throws Exception {
    IPropertyDescriptor propertyDescriptor = descriptor.createPropertyDescriptor();
    ICellEditorValidator validator = extractValidator( propertyDescriptor );

    String actual = validator.isValid( ATTRIBUTE_VALUE_LABEL );

    assertThat( actual ).isNull();
  }

  @Test
  public void validateInvalidInputString() throws Exception {
    String typeName = Integer.class.getName();
    IPropertyDescriptor propertyDescriptor = descriptor.createPropertyDescriptor();
    ICellEditorValidator validator = extractValidator( propertyDescriptor );

    String actual = validator.isValid( NOT_A_VALID_INPUT );

    assertThat( actual ).isEqualTo( format( ERROR_CONVERSION_TO_ATTRIBUTE_VALUE, NOT_A_VALID_INPUT, typeName ) );
  }

  @Test
  public void convertToRepresentationValue() {
    Object actual = descriptor.convertToRepresentationValue( Integer.valueOf( ATTRIBUTE_VALUE_LABEL ) );

    assertThat( actual ).isEqualTo( ATTRIBUTE_VALUE_LABEL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToRepresentationValueWithNullAsAttributeValueArgument() {
    descriptor.convertToRepresentationValue( null );
  }

  @Test
  public void convertToValue() {
    Object actual = descriptor.convertToValue( ATTRIBUTE_VALUE_LABEL );

    assertThat( actual ).isEqualTo( Integer.valueOf( ATTRIBUTE_VALUE_LABEL ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToValueWithNullAsAttributeValueLabelArgument() {
    descriptor.convertToValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new StandardDescriptor( null );
  }

  private static ICellEditorValidator extractValidator( IPropertyDescriptor descriptor ) throws Exception {
    IPropertyDescriptor delegate = getDelegate( descriptor );
    Field validatorField = delegate.getClass().getSuperclass().getDeclaredField( "validator" );
    validatorField.setAccessible( true );
    return ( ICellEditorValidator )validatorField.get( delegate );
  }
}