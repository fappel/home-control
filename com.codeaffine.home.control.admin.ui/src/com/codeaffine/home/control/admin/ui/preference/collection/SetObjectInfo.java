package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.info.AttributeActionType.DELETE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;


public class SetObjectInfo implements ObjectInfo {

  private final ModifyAdapter modifyAdapter;
  private final AttributeInfo attributeInfo;
  private final ObjectInfo objectInfo;
  private final Set<Object> set;

  @SuppressWarnings("unchecked")
  public SetObjectInfo( CollectionValue collectionValue, ModifyAdapter modifyAdapter ) {
    this.set = new HashSet<>( ( Set<Object>)collectionValue.getValue() );
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
        return getElementFor( attributeId ).getClass();
      }

      @Override
      public List<Class<?>> getGenericTypeParametersOfAttributeType() {
        return emptyList();
      }

      @Override
      public Set<AttributeAction> getActions() {
        return new HashSet<>( asList( new AttributeAction( DELETE, () -> removeElement( attributeId ) ) ) );
      }

      private void removeElement( Object attributeId ) {
        set.remove( getElementFor( attributeId  ) );
        objectInfo.setAttributeValue( attributeInfo.getName(), new HashSet<>( set ) );
        modifyAdapter.triggerUpdate();
      }
    };
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
    set.remove( getElementFor( attributeId ) );
    set.add( value );
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
    return getElementFor( attributeId );
  }

  @Override
  public List<AttributeInfo> getAttributeInfos() {
    return set
      .stream()
      .sorted()
      .map( element -> getAttributeInfo( element ) )
      .collect( toList() );
  }

  private Object getElementFor( Object attributeId ) {
    if( attributeId instanceof String ) {
      return set.stream().filter( element -> element.toString().equals( attributeId ) ).findFirst().get();
    }
    return attributeId;
  }

  @Override
  public Object getEditableValue() {
    return set;
  }
}