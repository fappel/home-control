package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.*;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.ATTRIBUTE_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;

public class ListAttributeInfoTest {

  private static final Integer ATTRIBUTE_ID = Integer.valueOf( 0 );

  private ListObjectInfo listObjectInfo;
  private ListAttributeInfo info;

  @Before
  public void setUp() {
    listObjectInfo = mock( ListObjectInfo.class );
    info = new ListAttributeInfo( listObjectInfo, ATTRIBUTE_ID );
  }

  @Test
  public void getName() {
    String actual = info.getName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_ID.toString() );
  }

  @Test
  public void getDisplayName() {
    String actual = info.getDisplayName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_ID.toString() );
  }

  @Test
  public void getAttributeType() {
    when( listObjectInfo.getAttributeValue( ATTRIBUTE_ID ) ).thenReturn( ATTRIBUTE_VALUE );

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

    assertThat( actual.stream().map( action -> action.getPresentation( CollectionAttributeActionPresentation.class ) ) )
      .containsExactly( UP, DOWN, DELETE  );
    verify( listObjectInfo ).removeListEntry( ATTRIBUTE_ID );
    verify( listObjectInfo ).moveListEntryUp( ATTRIBUTE_ID );
    verify( listObjectInfo ).moveListEntryDown( ATTRIBUTE_ID );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsListInfoObjectArgument() {
    new ListAttributeInfo( null, ATTRIBUTE_ID );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeIdArgument() {
    new ListAttributeInfo( listObjectInfo, null );
  }
}