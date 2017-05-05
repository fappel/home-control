package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class AttributeDescriptorSupplier {

  private final ObjectInfo info;

  public AttributeDescriptorSupplier( ObjectInfo info ) {
    verifyNotNull( info, "info" );

    this.info = info;
  }

  public AttributeDescriptor getDescriptor( Object attributeId ) {
    verifyNotNull( attributeId, "attributeName" );

    Class<?> type = info.getAttributeInfo( attributeId ).getAttributeType();
    if( SUPPORTED_COLLECTION_TYPES.contains( type ) ) {
      return new CollectionDescriptor( info, info.getAttributeInfo( attributeId ) );
    }
    if( String.class == type ) {
      return new TextDescriptor( info.getAttributeInfo( attributeId ) );
    }
    if( type.isEnum() ) {
      return new EnumDescriptor( info.getAttributeInfo( attributeId ) );
    }
    if( type == boolean.class || type == Boolean.class ) {
      return new BooleanDescriptor( info.getAttributeInfo( attributeId ) );
    }
    if( hasValueOfFactoryMethod( type ) ) {
      return new StandardDescriptor( info.getAttributeInfo( attributeId ) );
    }
    return new ReadOnlyDescriptor( info.getAttributeInfo( attributeId ) );
  }
}