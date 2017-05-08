package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class MapObjectInfo implements ObjectInfo {

  private final ModifyAdapter modifyAdapter;
  private final AttributeInfo attributeInfo;
  private final Map<Object, Object> map;
  private final ObjectInfo objectInfo;

  @SuppressWarnings("unchecked")
  public MapObjectInfo( CollectionValue collectionValue, ModifyAdapter modifyAdapter ) {
    verifyNotNull( collectionValue, "collectionValue" );
    verifyNotNull( modifyAdapter, "modifyAdapter" );

    this.map = new HashMap<>( ( Map<Object, Object> )collectionValue.getValue() );
    this.attributeInfo = collectionValue.getAttributeInfo();
    this.objectInfo = collectionValue.getObjectInfo();
    this.modifyAdapter = modifyAdapter;
  }

  @Override
  public AttributeInfo getAttributeInfo( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return new MapAttributeInfo( this, attributeId );
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
    verifyNotNull( attributeId, "attributeId" );

    map.put( getKeyFor( attributeId ), value );
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return map.get( getKeyFor( attributeId ) );
  }

  @Override
  public List<AttributeInfo> getAttributeInfos() {
    return map
      .keySet()
      .stream()
      .sorted()
      .map( attributeId -> getAttributeInfo( attributeId ) )
      .collect( toList() );
  }

  @Override
  public Object getEditableValue() {
    return new HashMap<>( map );
  }

  void removeMapEntry( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    map.remove( getKeyFor( attributeId  ) );
    objectInfo.setAttributeValue( attributeInfo.getName(), new HashMap<>( map ) );
    modifyAdapter.triggerUpdate();
  }


  private Object getKeyFor( Object attributeId ) {
    if( attributeId instanceof String ) {
      return map.keySet().stream().filter( key -> key.toString().equals( attributeId.toString() ) ).findFirst().get();
    }
    return attributeId;
  }
}