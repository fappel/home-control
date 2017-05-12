package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class SetObjectInfo implements ObjectInfo {

  private final ModifyAdapter modifyAdapter;
  private final AttributeInfo attributeInfo;
  private final ObjectInfo objectInfo;
  private final Set<Object> set;

  @SuppressWarnings("unchecked")
  public SetObjectInfo( CollectionValue collectionValue, ModifyAdapter modifyAdapter ) {
    verifyNotNull( collectionValue, "collectionValue" );
    verifyNotNull( modifyAdapter, "modifyAdapter" );

    this.set = new HashSet<>( ( Set<Object>)collectionValue.getValue() );
    this.attributeInfo = collectionValue.getAttributeInfo();
    this.objectInfo = collectionValue.getObjectInfo();
    this.modifyAdapter = modifyAdapter;
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
    verifyNotNull( attributeId, "attributeId" );

    set.remove( getElementFor( attributeId ) );
    set.add( value );
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

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

  @Override
  public AttributeInfo getAttributeInfo( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return new SetAttributeInfo( this, attributeId );
  }

  @Override
  public Object getEditableValue() {
    return new HashSet<>( set );
  }

  void removeElement( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    set.remove( getElementFor( attributeId  ) );
    objectInfo.setAttributeValue( attributeInfo.getName(), new HashSet<>( set ) );
    modifyAdapter.triggerUpdate();
  }

  private Object getElementFor( Object attributeId ) {
    if( attributeId instanceof String ) {
      return set.stream().filter( element -> element.toString().equals( attributeId ) ).findFirst().get();
    }
    return set.stream().filter( element -> element == attributeId ).findFirst().get();
  }
}