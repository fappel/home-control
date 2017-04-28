package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.preference.attribute.Messages.ERROR_CONVERSION_TO_ATTRIBUTE_VALUE;
import static com.codeaffine.home.control.admin.ui.test.AttributeDescriptorHelper.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.TextPropertyDescriptor;

public class StandardAdapterTest {

  private static final String ATTRIBUTE_VALUE_LABEL = "2";
  private static final String NOT_A_VALID_INPUT = "not-a-valid-input";

  private StandardAdapter adapter;

  @Before
  public void setUp() {
    adapter = new StandardAdapter( stubAttributeDescriptor( Integer.class ) );
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
  public void validateValidInputString() throws Exception {
    PropertyDescriptor descriptor = adapter.createPropertyDescriptor();
    ICellEditorValidator validator = extractValidator( descriptor );

    String actual = validator.isValid( ATTRIBUTE_VALUE_LABEL );

    assertThat( actual ).isNull();
  }

  @Test
  public void validateInvalidInputString() throws Exception {
    String typeName = Integer.class.getName();
    PropertyDescriptor descriptor = adapter.createPropertyDescriptor();
    ICellEditorValidator validator = extractValidator( descriptor );

    String actual = validator.isValid( NOT_A_VALID_INPUT );

    assertThat( actual ).isEqualTo( format( ERROR_CONVERSION_TO_ATTRIBUTE_VALUE, NOT_A_VALID_INPUT, typeName ) );
  }

  @Test
  public void convertToLabel() {
    Object actual = adapter.convertToLabel( Integer.valueOf( ATTRIBUTE_VALUE_LABEL ) );

    assertThat( actual ).isEqualTo( ATTRIBUTE_VALUE_LABEL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToLabelWithNullAsAttributeValueArgument() {
    adapter.convertToLabel( null );
  }

  @Test
  public void convertToAttributeValue() {
    Object actual = adapter.convertToAttributeValue( ATTRIBUTE_VALUE_LABEL );

    assertThat( actual ).isEqualTo( Integer.valueOf( ATTRIBUTE_VALUE_LABEL ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToAttributeValueWithNullAsAttributeValueLabelArgument() {
    adapter.convertToAttributeValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new StandardAdapter( null );
  }

  private static ICellEditorValidator extractValidator( PropertyDescriptor descriptor ) throws Exception {
    Field validatorField = descriptor.getClass().getSuperclass().getDeclaredField( "validator" );
    validatorField.setAccessible( true );
    return ( ICellEditorValidator )validatorField.get( descriptor );
  }
}