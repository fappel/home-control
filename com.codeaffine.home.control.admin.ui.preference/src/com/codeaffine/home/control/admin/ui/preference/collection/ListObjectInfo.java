package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class ListObjectInfo implements ObjectInfo {

  private final ModifyAdapter modifyAdapter;
  private final AttributeInfo attributeInfo;
  private final ObjectInfo objectInfo;
  private final List<Object> list;

  @SuppressWarnings("unchecked")
  public ListObjectInfo( CollectionValue collectionValue, ModifyAdapter modifyAdapter ) {
    verifyNotNull( collectionValue, "collectionValue" );
    verifyNotNull( modifyAdapter, "modifyAdapter" );

    this.list = new ArrayList<>( ( List<Object> )collectionValue.getValue() );
    this.attributeInfo = collectionValue.getAttributeInfo();
    this.objectInfo = collectionValue.getObjectInfo();
    this.modifyAdapter = modifyAdapter;
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
    verifyNotNull( attributeId, "attributeId" );

    list.set( getKeyFor( attributeId ), value );
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return list.get( getKeyFor( attributeId ) );
  }

  @Override
  public List<AttributeInfo> getAttributeInfos() {
    AtomicInteger counter = new AtomicInteger( 0 );
    return list
      .stream()
      .sorted()
      .map( element -> getAttributeInfo( Integer.valueOf( counter.getAndIncrement() ) ) )
      .collect( toList() );
  }

  @Override
  public AttributeInfo getAttributeInfo( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    return new ListAttributeInfo( this, attributeId );
  }

  @Override
  public Object getEditableValue() {
    return new ArrayList<>( list );
  }

  void removeListEntry( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    list.remove( getKeyFor( attributeId  ) );
    objectInfo.setAttributeValue( attributeInfo.getName(), new ArrayList<>( list ) );
    modifyAdapter.triggerUpdate();
  }

  void moveListEntryDown( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    int index = getKeyFor( attributeId );
    if( index < list.size() - 1 ) {
      Object element = list.remove( index );
      list.add( index + 1, element );
      objectInfo.setAttributeValue( attributeInfo.getName(), new ArrayList<>( list ) );
      modifyAdapter.triggerUpdate();
    }
  }

  void moveListEntryUp( Object attributeId ) {
    verifyNotNull( attributeId, "attributeId" );

    int index = getKeyFor( attributeId );
    if( index > 0 ) {
      Object element = list.remove( index );
      list.add( index - 1, element );
      objectInfo.setAttributeValue( attributeInfo.getName(), new ArrayList<>( list ) );
      modifyAdapter.triggerUpdate();
    }
  }

  private static int getKeyFor( Object attributeId ) {
    if( attributeId instanceof String ) {
      return Integer.valueOf( ( String )attributeId ).intValue();
    }
    return ( ( Integer )attributeId ).intValue();
  }
}