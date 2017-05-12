package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

@SuppressWarnings( "unchecked" )
public class MapObjectInfoTest {

  private static final Object KEY_1 = new Object();
  private static final Object VALUE_1 = new Object();
  private static final Object VALUE_2 = new Object();

  private CollectionValue collectionValue;
  private ModifyAdapter modifyAdapter;
  private Map<Object, Object> initialMap;
  private ObjectInfo objectInfo;

  @Before
  public void setUp() {
    initialMap = new HashMap<>();
    objectInfo = mock( ObjectInfo.class );
    collectionValue = new CollectionValue( objectInfo, stubAttributeInfo( Map.class ), initialMap );
    modifyAdapter = mock( ModifyAdapter.class );
  }

  @Test
  public void getAttributeValue() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getAttributeValue( KEY_1 );

    assertThat( actual ).isEqualTo( VALUE_1 );
  }

  @Test
  public void getAttributeValueWithStringAsAttributeIdArgument() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getAttributeValue( KEY_1.toString() );

    assertThat( actual ).isEqualTo( VALUE_1 );
  }

  @Test
  public void setAttributeValue() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    info.setAttributeValue( KEY_1, VALUE_2 );
    Object actual = info.getAttributeValue( KEY_1 );

    assertThat( actual ).isEqualTo( VALUE_2 );
  }

  @Test
  public void setAttributeValueWithStringAsAttributeIdArgument() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    info.setAttributeValue( KEY_1.toString(), VALUE_2 );
    Object actual = info.getAttributeValue( KEY_1.toString() );

    assertThat( actual ).isEqualTo( VALUE_2 );
  }

  @Test
  public void getAttributeInfo() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    AttributeInfo actual = info.getAttributeInfo( KEY_1 );

    assertThat( actual.getName() ).isEqualTo( KEY_1.toString() );
  }

  @Test
  public void getAttributeInfoWithStringAsAttributeIdArgument() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    AttributeInfo actual = info.getAttributeInfo( KEY_1.toString() );

    assertThat( actual.getName() ).isEqualTo( KEY_1.toString() );
  }

  @Test
  public void getAttributeInfos() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    List<AttributeInfo> actual = info.getAttributeInfos();

    assertThat( actual )
      .allMatch( attribute -> attribute.getName().equals( KEY_1.toString() ) );
  }

  @Test
  public void getAttributeInfosWithMultipleMapEntries() {
    initialMap.put( "b", VALUE_1 );
    initialMap.put( "a", VALUE_1 );
    initialMap.put( "d", VALUE_1 );
    initialMap.put( "c", VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    List<AttributeInfo> actual = info.getAttributeInfos();

    assertThat( actual.stream().map( attribute -> attribute.getName() ) )
     .isEqualTo( asList( "a", "b", "c", "d" ) );
  }

  @Test
  public void getEditableValue() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Map<Object, Object> actual = ( Map<Object, Object> )info.getEditableValue();

    assertThat( actual )
      .isEqualTo( initialMap )
      .isNotSameAs( initialMap );
  }

  @Test
  public void getEditableValueAndChangeOfReturnedMap() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Map<Object, Object> toChange = ( Map<Object, Object> )info.getEditableValue();
    toChange.put( KEY_1, VALUE_2 );
    Map<Object, Object> actual = ( Map<Object, Object> )info.getEditableValue();

    assertThat( actual )
      .isEqualTo( initialMap )
      .isNotEqualTo( toChange );
  }

  @Test
  public void removeMapEntry() {
    initialMap.put( KEY_1, VALUE_1 );
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    info.removeMapEntry( KEY_1 );
    Map<Object, Object> actual = ( Map<Object, Object> )info.getEditableValue();

    assertThat( actual ).isEmpty();
    InOrder order = inOrder( objectInfo, modifyAdapter );
    order.verify( objectInfo ).setAttributeValue( ATTRIBUTE_NAME, emptyMap() );
    order.verify( modifyAdapter ).triggerUpdate();
    order.verifyNoMoreInteractions();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsCollectionValueArgument() {
    new MapObjectInfo( null, modifyAdapter );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsModifyAdapterArgument() {
    new MapObjectInfo( collectionValue, null );
  }

  @Test
  public void getAttributeValueWithNullAsAttributeIdArgument() {
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getAttributeValue( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void setAttributeValueWithNullAsAttributeIdArgument() {
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.setAttributeValue( null, VALUE_1 ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void getAttributeInfoWithNullAsAttributeIdArgument() {
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getAttributeInfo( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void removeMapEntryWithNullAsAttributeIdArgument() {
    MapObjectInfo info = new MapObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.removeMapEntry( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }
}