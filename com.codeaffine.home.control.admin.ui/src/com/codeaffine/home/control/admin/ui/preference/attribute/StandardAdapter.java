package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.preference.attribute.Messages.ERROR_CONVERSION_TO_ATTRIBUTE_VALUE;
import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.getValueOfFactoryMethod;
import static com.codeaffine.home.control.util.reflection.ReflectionUtil.invoke;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import java.lang.reflect.Method;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.TextPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.PreferenceAttributeDescriptorAdapter;

class StandardAdapter implements PreferenceAttributeDescriptorAdapter {

  private final PreferenceAttributeDescriptor attributeDescriptor;

  StandardAdapter( PreferenceAttributeDescriptor attributeDescriptor ) {
    verifyNotNull( attributeDescriptor, "attributeDescriptor" );

    this.attributeDescriptor = attributeDescriptor;
  }

  @Override
  public PropertyDescriptor createPropertyDescriptor() {
    String displayName = attributeDescriptor.getDisplayName();
    String name = attributeDescriptor.getName();
    TextPropertyDescriptor result = new TextPropertyDescriptor( name, displayName );
    result.setValidator( value -> validate( value ) );
    return result;
  }

  @Override
  public Object convertToLabel( Object attributeValue ) {
    verifyNotNull( attributeValue, "attributeValue" );

    return String.valueOf( attributeValue );
  }

  @Override
  public Object convertToAttributeValue( Object attributeValueLabel ) {
    verifyNotNull( attributeValueLabel, "attributeValueLabel" );

    Class<?> attributeType = attributeDescriptor.getAttributeType();
    Method factoryMethod = getValueOfFactoryMethod( attributeType );
    return invoke( factoryMethod, null, attributeValueLabel );
  }

  private String validate( Object value ) {
    try {
      convertToAttributeValue( value );
      return null;
    } catch( RuntimeException invalidValue ) {
      String typeName = attributeDescriptor.getAttributeType().getName();
      return format( ERROR_CONVERSION_TO_ATTRIBUTE_VALUE, value, typeName );
    }
  }

}