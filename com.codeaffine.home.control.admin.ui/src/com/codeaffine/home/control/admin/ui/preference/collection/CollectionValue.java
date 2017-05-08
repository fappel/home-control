package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class CollectionValue {

  private final AttributeInfo attributeInfo;
  private final ObjectInfo objectInfo;
  private final Object value;

  public CollectionValue( ObjectInfo objectInfo, AttributeInfo attributeInfo, Object value ) {
    verifyNotNull( attributeInfo, "attributeInfo" );
    verifyNotNull( objectInfo, "objectInfo" );
    verifyNotNull( value, "value" );

    this.attributeInfo = attributeInfo;
    this.objectInfo = objectInfo;
    this.value = value;
  }

  public AttributeInfo getAttributeInfo() {
    return attributeInfo;
  }

  public Object getValue() {
    return value;
  }

  public ObjectInfo getObjectInfo() {
    return objectInfo;
  }
}
