package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class SetObjectInfoTest {

  private static final String ELEMENT_1 = "element_1";
  private static final String ELEMENT_2 = "element_2";

  private CollectionValue collectionValue;
  private ModifyAdapter modifyAdapter;
  private Set<Object> initialSet;
  private ObjectInfo objectInfo;

  @Before
  public void setUp() {
    initialSet = new HashSet<>();
    objectInfo = mock( ObjectInfo.class );
    collectionValue = new CollectionValue( objectInfo, stubAttributeInfo( Set.class ), initialSet );
    modifyAdapter = mock( ModifyAdapter.class );
  }

  @Test
  public void getAttributeValue() {
    initialSet.add( ELEMENT_1 );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getAttributeValue( ELEMENT_1 );

    assertThat( actual ).isEqualTo( ELEMENT_1 );
  }

  @Test
  public void getAttributeInfo() {
    initialSet.add( ELEMENT_1 );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    AttributeInfo attributeInfo = info.getAttributeInfo( ELEMENT_1 );

    assertThat( attributeInfo.getName() ).isEqualTo( ELEMENT_1 );
  }

  @Test
  public void getAttributeInfos() {
    initialSet.add( ELEMENT_1 );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    List<AttributeInfo> actual = info.getAttributeInfos();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( attributeInfo -> attributeInfo.getName().equals( ELEMENT_1 ) );
  }

  @Test
  public void setAttributeValue() {
    initialSet.add( ELEMENT_1 );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    info.setAttributeValue( ELEMENT_1, ELEMENT_2 );
    Object actual = info.getAttributeValue( ELEMENT_2  );

    assertThat( actual ).isEqualTo( ELEMENT_2 );
    assertThat( ( Set<?> )info.getEditableValue() ).hasSize( 1 );
  }

  @Test
  public void getEditableValue() {
    initialSet.add( ELEMENT_1 );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getEditableValue();

    assertThat( actual )
      .isEqualTo( initialSet )
      .isNotSameAs( initialSet );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getEditableValueAndChangeResult() {
    initialSet.add( ELEMENT_1 );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Set<String> toBeChanged = ( Set<String> )info.getEditableValue();
    toBeChanged.add( ELEMENT_2 );
    Object actual = info.getEditableValue();

    assertThat( actual )
      .isEqualTo( initialSet )
      .isNotSameAs( initialSet );
  }

  @Test
  public void removeElement() {
    initialSet.add( ELEMENT_1 );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    info.removeElement( ELEMENT_1 );
    Set<?> actual = ( Set<?> )info.getEditableValue();

    assertThat( actual ).isEmpty();
    InOrder order = inOrder( objectInfo, modifyAdapter );
    order.verify( objectInfo ).setAttributeValue( eq( ATTRIBUTE_NAME ), eq( emptySet() ) );
    order.verify( modifyAdapter ).triggerUpdate();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void getElementWithStringAttributeId() {
    Object element = new Object() {
      @Override
      public String toString() {
        return ELEMENT_1;
      }
    };
    initialSet.add( element );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getElementFor( ELEMENT_1 );

    assertThat( actual ).isSameAs( element );
  }

  @Test
  public void getElementWithObjectAttributeId() {
    Object element = new Object();
    initialSet.add( element );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Object actual = info.getElementFor( element );

    assertThat( actual ).isSameAs( element );
  }

  @Test
  public void getElementWithObjectAttributeIdThatDoesNotExist() {
    Object element = new Object();
    initialSet.add( element );
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getElementFor( new Object() ) );

    assertThat( actual ).isInstanceOf( NoSuchElementException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsCollectionValueArgument() {
    new SetObjectInfo( null, modifyAdapter );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsModifyAdapterArgument() {
    new SetObjectInfo( collectionValue, null );
  }

  @Test
  public void setAttributeValueWithNullAsAttributeIdArgument() {
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.setAttributeValue( null, new Object() ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void getAttributeValueWithNullAsAttributeIdArgument() {
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getAttributeValue( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void getAttributeInfoWithNullAsAttributeIdArgument() {
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getAttributeInfo( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void getElementForWithNullAsAttributeIdArgument() {
    SetObjectInfo info = new SetObjectInfo( collectionValue, modifyAdapter );

    Throwable actual = thrownBy( () -> info.getElementFor( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }
}