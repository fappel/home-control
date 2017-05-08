package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

@SuppressWarnings( "unchecked" )
public class ListObjectInfoTest {

  private static final String ELEMENT_1 = "element_1";
  private static final String ELEMENT_2 = "element_2";
  private static final Integer INDEX_OF_FIRST_ELEMENT = Integer.valueOf( 0 );
  private static final Integer INDEX_OF_SECOND_ELEMENT = Integer.valueOf( 1 );

  private CollectionValue collectionValue;
  private ModifyAdapter modifyAdapter;
  private List<Object> initialList;
  private ObjectInfo objectInfo;

  @Before
  public void setUp() {
    initialList = new ArrayList<>();
    objectInfo = mock( ObjectInfo.class );
    collectionValue = new CollectionValue( objectInfo, stubAttributeInfo( List.class ), initialList );
    modifyAdapter = mock( ModifyAdapter.class );
  }

  @Test
  public void getAttributeValue() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getAttributeValue( INDEX_OF_FIRST_ELEMENT );

    assertThat( actual ).isEqualTo( ELEMENT_1 );
  }

  @Test
  public void getAttributeValueWithStringAsAttributeIdArgument() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getAttributeValue( INDEX_OF_FIRST_ELEMENT.toString() );

    assertThat( actual ).isEqualTo( ELEMENT_1 );
  }

  @Test
  public void setAttributeValue() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    info.setAttributeValue( INDEX_OF_FIRST_ELEMENT, ELEMENT_2 );
    Object actual = info.getAttributeValue( INDEX_OF_FIRST_ELEMENT );

    assertThat( actual ).isEqualTo( ELEMENT_2 );
  }

  @Test
  public void setAttributeValueWithStringAsAttributeIdArgument() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    info.setAttributeValue( INDEX_OF_FIRST_ELEMENT.toString(), ELEMENT_2 );
    Object actual = info.getAttributeValue( INDEX_OF_FIRST_ELEMENT.toString() );

    assertThat( actual ).isEqualTo( ELEMENT_2 );
  }

  @Test
  public void getAttributeInfo() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    AttributeInfo actual = info.getAttributeInfo( INDEX_OF_FIRST_ELEMENT );

    assertThat( actual.getName() ).isEqualTo( INDEX_OF_FIRST_ELEMENT.toString() );
  }

  @Test
  public void getAttributeInfoWithStringAsAttributeIdArgument() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    AttributeInfo actual = info.getAttributeInfo( INDEX_OF_FIRST_ELEMENT.toString() );

    assertThat( actual.getName() ).isEqualTo( INDEX_OF_FIRST_ELEMENT.toString() );
  }

  @Test
  public void getAttributeInfos() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    List<AttributeInfo> actual = info.getAttributeInfos();

    assertThat( actual )
      .allMatch( attribute -> attribute.getName().equals( info.getAttributeInfo( INDEX_OF_FIRST_ELEMENT ).getName() ) );
  }

  @Test
  public void getEditableValue() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    List<Object> actual = ( List<Object> )info.getEditableValue();

    assertThat( actual )
      .isEqualTo( initialList )
      .isNotSameAs( initialList );
  }

  @Test
  public void getEditableValueAndChangeOfReturnedList() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    List<Object> toChange = ( List<Object> )info.getEditableValue();
    toChange.add( ELEMENT_2 );
    List<Object> actual = ( List<Object> )info.getEditableValue();

    assertThat( actual )
      .isEqualTo( initialList )
      .isNotEqualTo( toChange );
  }

  @Test
  public void removeListEntry() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    info.removeListEntry( INDEX_OF_FIRST_ELEMENT );
    List<Object> actual = ( List<Object> )info.getEditableValue();

    assertThat( actual ).isEmpty();
    InOrder order = inOrder( objectInfo, modifyAdapter );
    order.verify( objectInfo ).setAttributeValue( eq( ATTRIBUTE_NAME ), eq( emptyList() ) );
    order.verify( modifyAdapter ).triggerUpdate();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void moveListEntryDown() {
    initialList.add( ELEMENT_1 );
    initialList.add( ELEMENT_2 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    info.moveListEntryDown( INDEX_OF_FIRST_ELEMENT );
    List<Object> actual = ( List<Object> )info.getEditableValue();

    assertThat( actual ).isEqualTo( asList( ELEMENT_2, ELEMENT_1 ) );
    InOrder order = inOrder( objectInfo, modifyAdapter );
    order.verify( objectInfo ).setAttributeValue( eq( ATTRIBUTE_NAME ), eq( asList( ELEMENT_2, ELEMENT_1 ) ) );
    order.verify( modifyAdapter ).triggerUpdate();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void moveListEntryDownOnLowestElement() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    info.moveListEntryDown( INDEX_OF_FIRST_ELEMENT );
    List<Object> actual = ( List<Object> )info.getEditableValue();

    assertThat( actual ).isEqualTo( asList( ELEMENT_1 ) );
    inOrder( objectInfo, modifyAdapter ).verifyNoMoreInteractions();
  }

  @Test
  public void moveListEntryUp() {
    initialList.add( ELEMENT_1 );
    initialList.add( ELEMENT_2 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    info.moveListEntryUp( INDEX_OF_SECOND_ELEMENT );
    List<Object> actual = ( List<Object> )info.getEditableValue();

    assertThat( actual ).isEqualTo( asList( ELEMENT_2, ELEMENT_1 ) );
    InOrder order = inOrder( objectInfo, modifyAdapter );
    order.verify( objectInfo ).setAttributeValue( eq( ATTRIBUTE_NAME ), eq( asList( ELEMENT_2, ELEMENT_1 ) ) );
    order.verify( modifyAdapter ).triggerUpdate();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void moveListEntryDownOnHighestElement() {
    initialList.add( ELEMENT_1 );
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    info.moveListEntryUp( INDEX_OF_FIRST_ELEMENT );
    List<Object> actual = ( List<Object> )info.getEditableValue();

    assertThat( actual ).isEqualTo( asList( ELEMENT_1 ) );
    inOrder( objectInfo, modifyAdapter ).verifyNoMoreInteractions();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsCollectionValueArgument() {
    new ListObjectInfo( null, modifyAdapter );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsModifyAdapterArgument() {
    new ListObjectInfo( collectionValue, null );
  }

  @Test
  public void getAttributeValueWithNullAsAttributeIdArgument() {
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getAttributeValue( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void setAttributeValueWithNullAsAttributeIdArgument() {
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.setAttributeValue( null, ELEMENT_1 ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void getAttributeInfoWithNullAsAttributeIdArgument() {
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getAttributeInfo( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }


  @Test
  public void removeListEntryWithNullAsAttributeIdArgument() {
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.removeListEntry( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void moveListEntryDownWithNullAsAttributeIdArgument() {
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.moveListEntryDown( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void moveListEntryUpWithNullAsAttributeIdArgument() {
    ListObjectInfo info = new ListObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.moveListEntryUp( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }
}