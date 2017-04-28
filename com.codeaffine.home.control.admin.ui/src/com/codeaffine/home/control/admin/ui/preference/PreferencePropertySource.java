package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.stream.Stream;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;

class PreferencePropertySource implements IPropertySource {

  private final PreferenceInfo info;

  PreferencePropertySource( PreferenceInfo info ) {
    verifyNotNull( info, "info" );

    this.info = info;
  }

  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    return Stream.of( new PropertyDescriptor( info.getName(), info.getName() ) ).toArray( IPropertyDescriptor[]::new );
  }

  @Override
  public boolean isPropertySet( Object id ) {
    verifyNotNull( id, "id" );

    return true;
  }

  @Override
  public Object getPropertyValue( Object id ) {
    verifyNotNull( id, "id" );

    return info;
  }

  @Override
  public void resetPropertyValue( Object id ) {
  }

  @Override
  public void setPropertyValue( Object id, Object value ) {
  }

  @Override
  public Object getEditableValue() {
    return null;
  }
}