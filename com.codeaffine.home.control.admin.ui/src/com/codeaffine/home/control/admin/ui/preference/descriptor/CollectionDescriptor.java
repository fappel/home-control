package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

class CollectionDescriptor implements AttributeDescriptor {

  private final AttributeInfo attributeInfo;
  private final ObjectInfo objectInfo;

  public CollectionDescriptor( ObjectInfo objectInfo, AttributeInfo attributeInfo ) {
    verifyNotNull( attributeInfo, "attributeInfo" );
    verifyNotNull( objectInfo, "objectInfo" );

    this.attributeInfo = attributeInfo;
    this.objectInfo = objectInfo;
  }

  @Override
  public IPropertyDescriptor createPropertyDescriptor() {
    String name = attributeInfo.getName();
    String displayName = attributeInfo.getDisplayName();
    PropertyDescriptor descriptor = new CollectionPropertyDescriptor( name, displayName );
    return new ActionBarAdapter( descriptor, attributeInfo );
  }

  @Override
  public Object convertToRepresentationValue( Object value ) {
    return new CollectionValue( objectInfo, attributeInfo, value );
  }

  @Override
  public Object convertToValue( Object object ) {
    return object;
  }
}