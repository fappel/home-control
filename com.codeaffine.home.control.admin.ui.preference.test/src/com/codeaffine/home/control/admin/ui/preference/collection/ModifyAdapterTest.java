package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.Messages.ERROR_UNSUPPORTED_COLLECTION_TYPE;
import static com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter.*;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Maps.newHashMap;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ModifyAdapterTest {

  private static final Object VALUE_TO_ADD = new Object();
  private static final Object KEY_TO_ADD = new Object();

  private Runnable action;
  private ModifyAdapter modifyAdapter;
  private BiConsumer<CollectionValue, Consumer<Map<String, Object>>> additionHandler;

  @Before
  public void setUp() {
    action = mock( Runnable.class );
    additionHandler = mock( BiConsumer.class );
    modifyAdapter = new ModifyAdapter( action, additionHandler );
  }

  @Test
  public void triggerUpdate() {
    modifyAdapter.triggerUpdate();

    verify( action ).run();
  }

  @Test
  public void handleAdditionWithCallbackSimulationOnSet() {
    HashSet<Object> initialCollection = new HashSet<>();
    ObjectInfo objectInfo = mock( ObjectInfo.class );
    CollectionValue collectionValue = createCollectionValue( objectInfo, Set.class, initialCollection );
    Map<String, Object> additionInfo = newHashMap( ADDITION_INFO_VALUE, VALUE_TO_ADD );

    modifyAdapter.handleAddition( collectionValue );
    simulateDialogCallback( collectionValue, additionInfo );
    Set actual = captureNewCollectionValue( Set.class, objectInfo );

    assertThat( actual )
      .containsExactly( VALUE_TO_ADD )
      .isNotSameAs( initialCollection );
    verify( action ).run();
  }

  @Test
  public void handleAdditionWithCallbackSimulationOnList() {
    List<Object> initialCollection = new ArrayList<>();
    ObjectInfo objectInfo = mock( ObjectInfo.class );
    CollectionValue collectionValue = createCollectionValue( objectInfo, List.class, initialCollection );
    Map<String, Object> additionInfo = newHashMap( ADDITION_INFO_VALUE, VALUE_TO_ADD );

    modifyAdapter.handleAddition( collectionValue );
    simulateDialogCallback( collectionValue, additionInfo );
    List actual = captureNewCollectionValue( List.class, objectInfo );

    assertThat( actual )
      .containsExactly( VALUE_TO_ADD )
      .isNotSameAs( initialCollection );
    verify( action ).run();
  }

  @Test
  public void handleAdditionWithCallbackSimulationOnMap() {
    Map<Object, Object> initialCollection = new HashMap<>();
    ObjectInfo objectInfo = mock( ObjectInfo.class );
    CollectionValue collectionValue = createCollectionValue( objectInfo, Map.class, initialCollection );
    Map<String, Object> additionInfo = newHashMap( ADDITION_INFO_KEY, KEY_TO_ADD );
    additionInfo.put( ADDITION_INFO_VALUE, VALUE_TO_ADD );

    modifyAdapter.handleAddition( collectionValue );
    simulateDialogCallback( collectionValue, additionInfo );
    Map actual = captureNewCollectionValue( Map.class, objectInfo );

    assertThat( actual )
      .containsOnlyKeys( KEY_TO_ADD )
      .containsValue( VALUE_TO_ADD );
    verify( action ).run();
  }

  @Test
  public void handleAdditionWithCallbackSimulationOnUnsupportedCollectiontype() {
    Queue<Object> initialCollection = new LinkedList<>();
    ObjectInfo objectInfo = mock( ObjectInfo.class );
    CollectionValue collectionValue = createCollectionValue( objectInfo, Queue.class, initialCollection );
    Map<String, Object> additionInfo = newHashMap( ADDITION_INFO_VALUE, VALUE_TO_ADD );
    modifyAdapter.handleAddition( collectionValue );

    Throwable actual = thrownBy( () -> simulateDialogCallback( collectionValue, additionInfo ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_COLLECTION_TYPE, Queue.class.getName() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void handleAdditionWithNullAsCollectionValueArgument() {
    modifyAdapter.handleAddition( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDialogParentArgument() {
    new ModifyAdapter( null, action );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsUpdateActionArgument() {
    new ModifyAdapter( mock( Shell.class ), null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdditionHandlerArgument() {
    new ModifyAdapter( action , null);
  }

  private static CollectionValue createCollectionValue(
    ObjectInfo objectInfo, Class<?> collectionType, Object initialCollectionValue )
  {
    AttributeInfo attributeInfo = stubAttributeInfo( collectionType );
    return new CollectionValue( objectInfo, attributeInfo, initialCollectionValue );
  }

  private void simulateDialogCallback( CollectionValue collectionValue, Map<String, Object> additionMeta ) {
    ArgumentCaptor<Consumer> callBackCaptor = forClass( Consumer.class );
    verify( additionHandler ).accept( eq( collectionValue ), callBackCaptor.capture() );
    Consumer<Map<String, Object>> callback = callBackCaptor.getValue();
    callback.accept( additionMeta );
  }

  private static <T> T captureNewCollectionValue( Class<T> collectionType, ObjectInfo objectInfo ) {
    ArgumentCaptor<?> collectionCaptor = forClass( collectionType );
    verify( objectInfo ).setAttributeValue( eq( ATTRIBUTE_NAME ), collectionCaptor.capture() );
    return collectionType.cast( collectionCaptor.getValue() );
  }
}