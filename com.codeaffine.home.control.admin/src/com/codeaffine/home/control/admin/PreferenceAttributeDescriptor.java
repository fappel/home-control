package com.codeaffine.home.control.admin;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.beans.PropertyDescriptor;

public class PreferenceAttributeDescriptor {

  private final PropertyDescriptor descriptor;

  PreferenceAttributeDescriptor( PropertyDescriptor descriptor ) {
    verifyNotNull( descriptor, "descriptor" );

    this.descriptor = descriptor;
  }

  public Class<?> getAttributeType() {
    return descriptor.getPropertyType();
  }

  public String getName() {
    return descriptor.getName();
  }

  public String getDisplayName() {
    return descriptor.getDisplayName();
  }
}
