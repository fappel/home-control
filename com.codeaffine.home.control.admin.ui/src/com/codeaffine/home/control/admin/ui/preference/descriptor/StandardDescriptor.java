package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.Messages.ERROR_CONVERSION_TO_ATTRIBUTE_VALUE;
import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.getValueOfFactoryMethod;
import static com.codeaffine.home.control.util.reflection.ReflectionUtil.invoke;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import java.lang.reflect.Method;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.TextPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class StandardDescriptor implements AttributeDescriptor {

  private final AttributeInfo attributeInfo;

  StandardDescriptor( AttributeInfo attributeInfo ) {
    verifyNotNull( attributeInfo, "attributeDescriptor" );

    this.attributeInfo = attributeInfo;
  }

  @Override
  public IPropertyDescriptor createPropertyDescriptor() {
    String displayName = attributeInfo.getDisplayName();
    String name = attributeInfo.getName();
    TextPropertyDescriptor descriptor = new TextPropertyDescriptor( name, displayName );
    descriptor.setValidator( value -> validate( value ) );
    return new AttributePropertyDescriptor( descriptor, attributeInfo );
  }

  @Override
  public Object convertToRepresentationValue( Object value ) {
    verifyNotNull( value, "value" );

    return String.valueOf( value );
  }

  @Override
  public Object convertToValue( Object label ) {
    verifyNotNull( label, "label" );

    Class<?> attributeType = attributeInfo.getAttributeType();
    Method factoryMethod = getValueOfFactoryMethod( attributeType );
    return invoke( factoryMethod, null, label );
  }

  private String validate( Object value ) {
    try {
      convertToValue( value );
      return null;
    } catch( RuntimeException invalidValue ) {
      String typeName = attributeInfo.getAttributeType().getName();
      return format( ERROR_CONVERSION_TO_ATTRIBUTE_VALUE, value, typeName );
    }
  }

}