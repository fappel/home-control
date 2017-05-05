package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class PreferenceObjectInfo implements ObjectInfo {

  private final PreferenceInfo preferenceInfo;
  private final ModifyAdapter modifyAdapter;

  public PreferenceObjectInfo( PreferenceInfo preferenceInfo, ModifyAdapter modifyAdapter ) {
    verifyNotNull( preferenceInfo, "preferenceInfo" );
    verifyNotNull( modifyAdapter, "modifyAdapter" );

    this.modifyAdapter = modifyAdapter;
    this.preferenceInfo = preferenceInfo;
  }

  @Override
  public AttributeInfo getAttributeInfo( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return new PreferenceObjectAttributeInfo( preferenceInfo, this, attributeId, modifyAdapter );
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
    verifyNotNull( attributeId, "attributeId" );

    preferenceInfo.setAttributeValue( ( String )attributeId, value );
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return preferenceInfo.getAttributeValue( ( String )attributeId );
  }

  @Override
  public List<AttributeInfo> getAttributeInfos() {
    return preferenceInfo.getAttributeDescriptors()
      .stream()
      .map( descriptor -> getAttributeInfo( descriptor.getName() ) )
      .collect( toList() );
  }

  @Override
  public Object getEditableValue() {
    return null;
  }
}