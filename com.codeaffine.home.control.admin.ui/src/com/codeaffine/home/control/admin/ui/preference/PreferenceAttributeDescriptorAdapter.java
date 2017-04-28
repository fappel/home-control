package com.codeaffine.home.control.admin.ui.preference;

import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;

public interface PreferenceAttributeDescriptorAdapter {
  PropertyDescriptor createPropertyDescriptor();
  Object convertToLabel( Object attributeValue );
  Object convertToAttributeValue( Object attributeValueLabel );
}