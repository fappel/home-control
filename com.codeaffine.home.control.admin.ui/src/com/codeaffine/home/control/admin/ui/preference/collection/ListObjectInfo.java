package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class ListObjectInfo implements ObjectInfo {

  private final ModifyAdapter modifyAdapter;
  private final AttributeInfo attributeInfo;
  private final ObjectInfo objectInfo;
  private final List<Object> list;

  @SuppressWarnings("unchecked")
  public ListObjectInfo( CollectionValue collectionValue, ModifyAdapter modifyAdapter ) {
    this.list = new ArrayList<>( ( List<Object> )collectionValue.getValue() );
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
        return list.get( getKeyFor( attributeId ) ).getClass();
      }

      @Override
      public List<Class<?>> getGenericTypeParametersOfAttributeType() {
        return emptyList();
      }

      @Override
      public List<AttributeAction> getActions() {
        return asList( new AttributeAction( () -> moveListEntryUp( attributeId ), UP ),
                       new AttributeAction( () -> moveListEntryDown( attributeId ), DOWN ),
                       new AttributeAction( () -> removeListEntry( attributeId ), DELETE ) ) ;
      }

      private void removeListEntry( Object attributeId ) {
        list.remove( getKeyFor( attributeId  ) );
        objectInfo.setAttributeValue( attributeInfo.getName(), new ArrayList<>( list ) );
        modifyAdapter.triggerUpdate();
      }

      private void moveListEntryDown( Object attributeId ) {
        int index = getKeyFor( attributeId );
        if( index < list.size() - 1 ) {
          Object element = list.remove( index );
          list.add( index + 1, element );
        }
        objectInfo.setAttributeValue( attributeInfo.getName(), new ArrayList<>( list ) );
        modifyAdapter.triggerUpdate();
      }

      private void moveListEntryUp( Object attributeId ) {
        int index = getKeyFor( attributeId );
        if( index > 0 ) {
          Object element = list.remove( index );
          list.add( index - 1, element );
        }
        objectInfo.setAttributeValue( attributeInfo.getName(), new ArrayList<>( list ) );
        modifyAdapter.triggerUpdate();
      }
    };
  }

  @Override
  public void setAttributeValue( Object attributeId, Object value ) {
    list.set( getKeyFor( attributeId ), value );
  }

  @Override
  public Object getAttributeValue( Object attributeId ) {
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

  private static int getKeyFor( Object attributeId ) {
    if( attributeId instanceof String ) {
      return Integer.valueOf( ( String )attributeId ).intValue();
    }
    return ( ( Integer )attributeId ).intValue();
  }

  @Override
  public Object getEditableValue() {
    return list;
  }
}