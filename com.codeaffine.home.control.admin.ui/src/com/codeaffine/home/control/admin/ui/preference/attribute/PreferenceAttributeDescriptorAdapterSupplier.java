package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.hasValueOfFactoryMethod;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.PreferenceAttributeDescriptorAdapter;

public class PreferenceAttributeDescriptorAdapterSupplier {

  private final PreferenceInfo info;

  public PreferenceAttributeDescriptorAdapterSupplier( PreferenceInfo info ) {
    verifyNotNull( info, "info" );

    this.info = info;
  }

  public PreferenceAttributeDescriptorAdapter getAttributeDescriptorAdapter( String attributeName ) {
    verifyNotNull( attributeName, "attributeName" );

    Class<?> type = info.getAttributeDescriptor( attributeName ).getAttributeType();
    if( String.class == type ) {
      return new TextAdapter( info.getAttributeDescriptor( attributeName ) );
    }
    if( type.isEnum() ) {
      return new EnumAdapter( info.getAttributeDescriptor( attributeName ) );
    }
    if( type == boolean.class || type == Boolean.class ) {
      return new BooleanAdapter( info.getAttributeDescriptor( attributeName ) );
    }
    if( hasValueOfFactoryMethod( type ) ) {
      return new StandardAdapter( info.getAttributeDescriptor( attributeName ) );
    }
    return new ReadOnlyAdapter( info.getAttributeDescriptor( attributeName ) );
  }
}