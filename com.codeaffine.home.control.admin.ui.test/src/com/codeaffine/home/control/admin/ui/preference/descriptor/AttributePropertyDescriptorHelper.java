package com.codeaffine.home.control.admin.ui.preference.descriptor;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;

class AttributePropertyDescriptorHelper {
  
  static boolean hasExactlyDelegateInstanceOf( IPropertyDescriptor descriptor, Class<?> expectedType ) {
    return expectedType == getDelegate( descriptor ).getClass();
  }

  static IPropertyDescriptor getDelegate( IPropertyDescriptor descriptor ) {
    return ( ( ActionBarAdapter )descriptor ).getDelegate();
  }
}