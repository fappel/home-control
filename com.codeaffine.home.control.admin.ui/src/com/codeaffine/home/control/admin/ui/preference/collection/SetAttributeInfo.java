package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.DELETE;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class SetAttributeInfo implements AttributeInfo {

  private final SetObjectInfo setObjectInfo;
  private final Object attributeId;

  SetAttributeInfo( SetObjectInfo setObjectInfo, Object attributeId ) {
    verifyNotNull( setObjectInfo, "setObjectInfo" );
    verifyNotNull( attributeId, "attributeId" );

    this.setObjectInfo = setObjectInfo;
    this.attributeId = attributeId;
  }

  @Override
  public String getName() {
    return attributeId.toString();
  }

  @Override
  public String getDisplayName() {
    return attributeId.toString();
  }

  @Override
  public Class<?> getAttributeType() {
    return setObjectInfo.getElementFor( attributeId ).getClass();
  }

  @Override
  public List<Class<?>> getGenericTypeParametersOfAttributeType() {
    return emptyList();
  }

  @Override
  public List<AttributeAction> getActions() {
    return asList( new AttributeAction( () -> setObjectInfo.removeElement( attributeId ), DELETE ) );
  }
}