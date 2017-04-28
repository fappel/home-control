package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.TextPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.PreferenceAttributeDescriptorAdapter;

class TextAdapter implements PreferenceAttributeDescriptorAdapter {

  private final PreferenceAttributeDescriptor attributeDescriptor;

  public TextAdapter( PreferenceAttributeDescriptor attributeDescriptor ) {
    verifyNotNull( attributeDescriptor, "attributeDescriptor" );

    this.attributeDescriptor = attributeDescriptor;
  }

  @Override
  public PropertyDescriptor createPropertyDescriptor() {
    return new TextPropertyDescriptor( attributeDescriptor.getName(), attributeDescriptor.getDisplayName() );
  }

  @Override
  public Object convertToLabel( Object attributeValue ) {
    verifyNotNull( attributeValue, "attributeValue" );

    return String.valueOf( attributeValue );
  }

  @Override
  public Object convertToAttributeValue( Object attributeValueLabel ) {
    verifyNotNull( attributeValueLabel, "attributeValueLabel" );

    return attributeValueLabel;
  }
}