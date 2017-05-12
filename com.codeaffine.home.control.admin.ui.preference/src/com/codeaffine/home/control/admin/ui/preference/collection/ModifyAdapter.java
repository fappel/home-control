package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.Messages.ERROR_UNSUPPORTED_COLLECTION_TYPE;
import static com.codeaffine.home.control.util.reflection.AttributeReflectionUtil.SUPPORTED_COLLECTION_TYPES;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.ui.preference.collection.dialog.AddElementDialog;

public class ModifyAdapter {

  public static final String ADDITION_INFO_VALUE = "Value";
  public static final String ADDITION_INFO_KEY = "Key";

  private final BiConsumer<CollectionValue, Consumer<Map<String, Object>>> additionHandler;
  private final Runnable updateAction;

  public ModifyAdapter( Shell dialogParent, Runnable updateAction ) {
    this( updateAction, ( value, additionCallback ) -> AddElementDialog.open( dialogParent, value, additionCallback ) );
    verifyNotNull( dialogParent, "dialogParent" );
  }

  ModifyAdapter(
    Runnable updateAction, BiConsumer<CollectionValue, Consumer<Map<String, Object>>> additionHandler )
  {
    verifyNotNull( additionHandler, "additionHandler" );
    verifyNotNull( updateAction, "updateAction" );

    this.additionHandler = additionHandler;
    this.updateAction = updateAction;
  }

  public void triggerUpdate() {
    updateAction.run();
  }

  public void handleAddition( CollectionValue collectionValue ) {
    verifyNotNull( collectionValue, "collectionValue" );

    additionHandler.accept( collectionValue, additionInfo -> handleAddition( collectionValue, additionInfo ) );
  }

  @SuppressWarnings("unchecked")
  private void handleAddition( CollectionValue collectionValue, Map<String, Object> additionInfo ) {
    Class<?> attributeType = getCollectionType( collectionValue );
    Object value = null;
    if( attributeType == Set.class ) {
      Set<Object> set = new HashSet<>( ( Set<Object> )collectionValue.getValue() );
      set.add( additionInfo.get( ADDITION_INFO_VALUE ) );
      value = set;
    }
    if( attributeType == List.class ) {
      List<Object> list = new ArrayList<>( ( List<Object> )collectionValue.getValue() );
      list.add( additionInfo.get( ADDITION_INFO_VALUE ) );
      value = list;
    }
    if( attributeType == Map.class ) {
      Map<Object, Object> map = new HashMap<>( ( Map<Object, Object> )collectionValue.getValue() );
      map.put( additionInfo.get( ADDITION_INFO_KEY ), additionInfo.get( ADDITION_INFO_VALUE ) );
      value = map;
    }
    collectionValue.getObjectInfo().setAttributeValue( collectionValue.getAttributeInfo().getName(), value );
    triggerUpdate();
  }

  private static Class<?> getCollectionType( CollectionValue collectionValue ) {
    Class<?> result = collectionValue.getAttributeInfo().getAttributeType();
    if( !SUPPORTED_COLLECTION_TYPES.contains( result ) ) {
      throw new IllegalStateException( format( ERROR_UNSUPPORTED_COLLECTION_TYPE, result.getName() ) );
    }
    return result;
  }
}