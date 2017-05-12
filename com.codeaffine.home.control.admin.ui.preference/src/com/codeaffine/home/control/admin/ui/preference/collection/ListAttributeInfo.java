package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class ListAttributeInfo implements AttributeInfo {

  private final ListObjectInfo listObjectInfo;
  private final Object attributeId;

  ListAttributeInfo( ListObjectInfo listObjectInfo, Object attributeId ) {
    verifyNotNull( listObjectInfo, "listObjectInfo" );
    verifyNotNull( attributeId, "attributeId" );

    this.listObjectInfo = listObjectInfo;
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
    return listObjectInfo.getAttributeValue( attributeId  ).getClass();
  }

  @Override
  public List<Class<?>> getGenericTypeParametersOfAttributeType() {
    return emptyList();
  }

  @Override
  public List<AttributeAction> getActions() {
    return asList( new AttributeAction( () -> listObjectInfo.moveListEntryUp( attributeId ), UP ),
                   new AttributeAction( () -> listObjectInfo.moveListEntryDown( attributeId ), DOWN ),
                   new AttributeAction( () -> listObjectInfo.removeListEntry( attributeId ), DELETE ) ) ;
  }
}