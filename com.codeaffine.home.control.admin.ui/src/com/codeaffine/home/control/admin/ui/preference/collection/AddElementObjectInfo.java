package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptyList;

import java.util.List;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

class AddElementObjectInfo implements ObjectInfo {

  private final Class<?> elementType;

  public AddElementObjectInfo( Class<?> elementType ) {
    verifyNotNull( elementType, "elementType" );

    this.elementType = elementType;
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
  }

  @Override
  public Object getEditableValue() {
    return null;
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
    return null;
  }

  @Override
  public List<AttributeInfo> getAttributeInfos() {
    return null;
  }

  @Override
  public AttributeInfo getAttributeInfo( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return new AttributeInfo() {

      @Override
      public String getName() {
        return attributeId.toString();
      }

      @Override
      public List<Class<?>> getGenericTypeParametersOfAttributeType() {
        return emptyList();
      }

      @Override
      public String getDisplayName() {
        return attributeId.toString();
      }

      @Override
      public Class<?> getAttributeType() {
        return elementType;
      }

      @Override
      public List<AttributeAction> getActions() {
        return emptyList();
      }
    };
  }
}