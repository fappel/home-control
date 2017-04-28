package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.preference.attribute.Messages.ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.PreferenceAttributeDescriptorAdapter;

class ReadOnlyAdapter implements PreferenceAttributeDescriptorAdapter {

  private final PreferenceAttributeDescriptor attributeDescriptor;

  ReadOnlyAdapter( PreferenceAttributeDescriptor attributeDescriptor ) {
    verifyNotNull( attributeDescriptor, "attributeDescriptor" );

    this.attributeDescriptor = attributeDescriptor;
  }

  @Override
  public PropertyDescriptor createPropertyDescriptor() {
    return new PropertyDescriptor( attributeDescriptor.getName(), attributeDescriptor.getDisplayName() );
  }

  @Override
  public Object convertToLabel( Object attributeValue ) {
    verifyNotNull( attributeValue, "attributeValue" );

    return String.valueOf( attributeValue );
  }

  @Override
  public Object convertToAttributeValue( Object attributeValueLabel ) {
    verifyNotNull( attributeValueLabel, "attributeValueLabel" );

    String typeName = attributeDescriptor.getAttributeType().getName();
    String message = format( ERROR_UNSUPPORTED_TEXT_TO_OBJECT_CONVERSION, attributeValueLabel, typeName );
    throw new UnsupportedOperationException( message );
  }
}