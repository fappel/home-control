package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.DELETE;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.ATTRIBUTE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;

public class SetAttributeInfoTest {

  private SetObjectInfo setObjectInfo;
  private SetAttributeInfo info;

  @Before
  public void setUp() {
    setObjectInfo = mock( SetObjectInfo.class );
    info = new SetAttributeInfo( setObjectInfo, ATTRIBUTE_NAME );
  }

  @Test
  public void getName() {
    String actual = info.getName();

    assertThat( actual ).isSameAs( ATTRIBUTE_NAME );
  }

  @Test
  public void getDisplayName() {
    String actual = info.getDisplayName();

    assertThat( actual ).isSameAs( ATTRIBUTE_NAME );
  }

  @Test
  public void getAttributeType() {
    when( setObjectInfo.getElementFor( ATTRIBUTE_NAME ) ).thenReturn( ATTRIBUTE_NAME );

    Class<?> actual = info.getAttributeType();

    assertThat( actual ).isSameAs( ATTRIBUTE_NAME.getClass() );
  }

  @Test
  public void getGenericTypeParametersOfAttributeType() {
    List<Class<?>> actual = info.getGenericTypeParametersOfAttributeType();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getActionsAndInvoke() {
    List<AttributeAction> actions = info.getActions();
    actions.forEach( action -> action.run() );

    assertThat( actions )
      .allMatch( action -> action.getPresentation( CollectionAttributeActionPresentation.class ) == DELETE );
    verify( setObjectInfo ).removeElement( ATTRIBUTE_NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSetObjectInfoArgument() {
    new SetAttributeInfo( null, ATTRIBUTE_NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeIdArgument() {
    new SetAttributeInfo( setObjectInfo, null );
  }
}