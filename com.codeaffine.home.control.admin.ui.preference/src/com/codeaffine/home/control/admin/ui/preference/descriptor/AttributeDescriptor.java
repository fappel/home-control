package com.codeaffine.home.control.admin.ui.preference.descriptor;

import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;

public interface AttributeDescriptor {
  public IPropertyDescriptor createPropertyDescriptor();
  public Object convertToRepresentationValue( Object value );
  public Object convertToValue( Object label );
}