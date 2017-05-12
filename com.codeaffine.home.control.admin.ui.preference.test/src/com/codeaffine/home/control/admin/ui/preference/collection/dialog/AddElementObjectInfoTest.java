package com.codeaffine.home.control.admin.ui.preference.collection.dialog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.collection.dialog.AddElementObjectInfo;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

public class AddElementObjectInfoTest {

  private static final String ANY_ATTRIBUTE_ID = "anyAttributeId";

  private AddElementObjectInfo info;

  @Before
  public void setUp() {
    info = new AddElementObjectInfo( Integer.class );
  }

  @Test
  public void getAttributeInfo() {
    AttributeInfo attributeInfo = info.getAttributeInfo( ANY_ATTRIBUTE_ID );

    assertThat( attributeInfo.getName() ).isEqualTo( ANY_ATTRIBUTE_ID );
    assertThat( attributeInfo.getDisplayName() ).isEqualTo( ANY_ATTRIBUTE_ID );
    assertThat( attributeInfo.getAttributeType() ).isSameAs( Integer.class );
    assertThat( attributeInfo.getGenericTypeParametersOfAttributeType() ).isEmpty();
    assertThat( attributeInfo.getActions() ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void getAttributeInfoWithNullAsAttributeIdArgument() {
    info.getAttributeInfo( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsElementTypeArgument() {
    new AddElementObjectInfo( null );
  }
}