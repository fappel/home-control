package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.DELETE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class MapObjectInfo implements ObjectInfo {

  private final ModifyAdapter modifyAdapter;
  private final AttributeInfo attributeInfo;
  private final Map<Object, Object> map;
  private final ObjectInfo objectInfo;

  @SuppressWarnings("unchecked")
  public MapObjectInfo( CollectionValue collectionValue, ModifyAdapter modifyAdapter ) {
    this.map = new HashMap<>( ( Map<Object, Object> )collectionValue.getValue() );
    this.attributeInfo = collectionValue.getAttributeInfo();
    this.objectInfo = collectionValue.getObjectInfo();
    this.modifyAdapter = modifyAdapter;
  }

  @Override
  public AttributeInfo getAttributeInfo( Object attributeId ) {
    return new AttributeInfo() {

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
        return map.get( getKeyFor( attributeId ) ).getClass();
      }

      @Override
      public List<Class<?>> getGenericTypeParametersOfAttributeType() {
        return emptyList();
      }

      @Override
      public List<AttributeAction> getActions() {
        return asList( new AttributeAction( () -> removeMapEntry( attributeId ), DELETE ) );
      }

      private void removeMapEntry( Object attributeId ) {
        map.remove( getKeyFor( attributeId  ) );
        objectInfo.setAttributeValue( attributeInfo.getName(), new HashMap<>( map ) );
        modifyAdapter.triggerUpdate();
      }
    };
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
    map.put( getKeyFor( attributeId ), value );
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
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

  private Object getKeyFor( Object attributeId ) {
    if( attributeId instanceof String ) {
      return map.keySet().stream().filter( key -> key.toString().equals( attributeId.toString() ) ).findFirst().get();
    }
    return attributeId;
  }

  @Override
  public Object getEditableValue() {
    return map;
  }
}
