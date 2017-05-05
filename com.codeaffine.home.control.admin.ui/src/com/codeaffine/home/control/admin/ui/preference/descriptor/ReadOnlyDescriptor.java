package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.Messages.ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class ReadOnlyDescriptor implements AttributeDescriptor {

  private final AttributeInfo attributeInfo;

  ReadOnlyDescriptor( AttributeInfo attributeInfo ) {
    verifyNotNull( attributeInfo, "attributeDescriptor" );

    this.attributeInfo = attributeInfo;
  }

  @Override
  public IPropertyDescriptor createPropertyDescriptor() {
    PropertyDescriptor descriptor = new PropertyDescriptor( attributeInfo.getName(), attributeInfo.getDisplayName() );
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

    String typeName = attributeInfo.getAttributeType().getName();
    String message = format( ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION, label, typeName );
    throw new UnsupportedOperationException( message );
  }
}