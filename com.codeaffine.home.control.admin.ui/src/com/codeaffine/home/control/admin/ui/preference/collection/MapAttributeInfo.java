package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.DELETE;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class MapAttributeInfo implements AttributeInfo {

  private final MapObjectInfo mapObjectInfo;
  private final Object attributeId;

  public MapAttributeInfo( MapObjectInfo mapObjectInfo, Object attributeId ) {
    verifyNotNull( mapObjectInfo, "mapObjectInfo" );
    verifyNotNull( attributeId, "attributeId" );

    this.mapObjectInfo = mapObjectInfo;
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
    return mapObjectInfo.getAttributeValue( attributeId ).getClass();
  }

  @Override
  public List<Class<?>> getGenericTypeParametersOfAttributeType() {
    return emptyList();
  }

  @Override
  public List<AttributeAction> getActions() {
    return asList( new AttributeAction( () -> mapObjectInfo.removeMapEntry( attributeId ), DELETE ) );
  }
}