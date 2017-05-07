package com.codeaffine.home.control.admin.ui.preference.collection;

import static java.util.Collections.emptyList;

import java.util.List;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

class AddElementObjectInfo implements ObjectInfo {

  private static final String ATTRIBUTE_NAME = "Attribute";

  private final Class<?> elementType;

  public AddElementObjectInfo( Class<?> elementType ) {
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
    return new AttributeInfo() {

      @Override
      public String getName() {
        return ATTRIBUTE_NAME;
      }

      @Override
      public List<Class<?>> getGenericTypeParametersOfAttributeType() {
        return null;
      }

      @Override
      public String getDisplayName() {
        return ATTRIBUTE_NAME;
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