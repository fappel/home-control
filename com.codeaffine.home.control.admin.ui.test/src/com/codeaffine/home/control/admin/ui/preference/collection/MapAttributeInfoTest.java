package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.DELETE;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;

public class MapAttributeInfoTest {

  private MapObjectInfo mapObjectInfo;
  private MapAttributeInfo info;

  @Before
  public void setUp() {
    mapObjectInfo = mock( MapObjectInfo.class );
    info = new MapAttributeInfo( mapObjectInfo, ATTRIBUTE_NAME );
  }

  @Test
  public void getName() {
    String actual = info.getName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_NAME );
  }

  @Test
  public void getDisplayName() {
    String actual = info.getDisplayName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_NAME );
  }

  @Test
  public void getAttributeType() {
    when( mapObjectInfo.getAttributeValue( ATTRIBUTE_NAME ) ).thenReturn( ATTRIBUTE_VALUE );

    Class<?> actual = info.getAttributeType();

    assertThat( actual ).isSameAs( ATTRIBUTE_VALUE.getClass() );
  }

  @Test
  public void getGenericTypeParametersOfAttributeType() {
    List<Class<?>> actual = info.getGenericTypeParametersOfAttributeType();

    assertThat( actual ).isEmpty();
  }
  @Test
  public void getActionsAndInvoke() {
    List<AttributeAction> actual = info.getActions();
    actual.forEach( action -> action.run() );

    assertThat( actual )
      .allMatch( action -> action.getPresentation( CollectionAttributeActionPresentation.class ) == DELETE );
    verify( mapObjectInfo ).removeMapEntry( ATTRIBUTE_NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsCollectionValueArgument() {
    new MapAttributeInfo( null, ATTRIBUTE_NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeIdArgument() {
    new MapAttributeInfo( mapObjectInfo, null );
  }
}