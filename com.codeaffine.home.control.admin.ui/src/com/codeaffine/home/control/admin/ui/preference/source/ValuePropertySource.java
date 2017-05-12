package com.codeaffine.home.control.admin.ui.preference.source;

import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySource;

public class ValuePropertySource implements IPropertySource {

  private static final IPropertyDescriptor[] EMPTY_DESCRIPTORS = new IPropertyDescriptor[ 0 ];

  private final Object value;

  public ValuePropertySource( Object value ) {
    this.value = value;
  }

  @Override
  public Object getEditableValue() {
    return value;
  }

  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    return EMPTY_DESCRIPTORS;
  }

  @Override
  public Object getPropertyValue( Object id ) {
    return null;
  }

  @Override
  public boolean isPropertySet( Object id ) {
    return false;
  }

  @Override
  public void resetPropertyValue( Object id ) {
  }

  @Override
  public void setPropertyValue( Object id, Object value ) {
  }
}
