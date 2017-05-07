package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.ADD;
import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.SUPPORTED_COLLECTION_TYPES;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class PreferenceObjectAttributeInfo implements AttributeInfo {

  private final PreferenceObjectInfo objectInfo;
  private final PreferenceInfo preferenceInfo;
  private final ModifyAdapter modifyAdapter;
  private final Object attributeId;

  PreferenceObjectAttributeInfo(
    PreferenceInfo preferenceInfo, PreferenceObjectInfo objectInfo, Object attributeId, ModifyAdapter modifyAdapter )
  {
    verifyNotNull( preferenceInfo, "preferenceInfo" );
    verifyNotNull( objectInfo, "objectInfo" );
    verifyNotNull( attributeId, "attributeId" );
    verifyNotNull( modifyAdapter, "modifyAdapter" );

    this.preferenceInfo = preferenceInfo;
    this.modifyAdapter = modifyAdapter;
    this.attributeId = attributeId;
    this.objectInfo = objectInfo;
  }

  @Override
  public String getName() {
    return preferenceInfo.getAttributeDescriptor( ( String )attributeId ).getName();
  }

  @Override
  public String getDisplayName() {
    return preferenceInfo.getAttributeDescriptor( ( String )attributeId ).getDisplayName();
  }

  @Override
  public Class<?> getAttributeType() {
    return preferenceInfo.getAttributeDescriptor( ( String )attributeId ).getAttributeType();
  }

  @Override
  public List<Class<?>> getGenericTypeParametersOfAttributeType() {
    return preferenceInfo.getAttributeDescriptor( ( String )attributeId ).getGenericTypeParametersOfAttributeType();
  }

  @Override
  public List<AttributeAction> getActions() {
    if( SUPPORTED_COLLECTION_TYPES.stream().anyMatch( type -> getAttributeType().isAssignableFrom( type ) ) ) {
      return asList( new AttributeAction( () -> add(), ADD ) );
    }
    return emptyList();
  }

  private void add() {
    modifyAdapter.handleAddition( newCollectionValue() );
  }

  private CollectionValue newCollectionValue() {
    return new CollectionValue( objectInfo, this, objectInfo.getAttributeValue( attributeId ) );
  }
}