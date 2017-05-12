package com.codeaffine.home.control.admin.ui.preference.info;

import java.util.List;

public interface ObjectInfo {
  AttributeInfo getAttributeInfo( Object attributeId );
  void setAttributeValue( Object attributeId, Object value );
  Object getAttributeValue( Object attributeId );
  List<AttributeInfo> getAttributeInfos();
  Object getEditableValue();
}