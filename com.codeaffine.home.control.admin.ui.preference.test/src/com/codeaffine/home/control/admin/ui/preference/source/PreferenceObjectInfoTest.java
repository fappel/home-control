package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.home.control.admin.ui.test.PreferenceInfoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

public class PreferenceObjectInfoTest {

  private PreferenceObjectInfo objectInfo;
  private PreferenceInfo preferenceInfo;
  private ModifyAdapter modifyAdapter;

  @Before
  public void setUp() {
    PreferenceAttributeDescriptor descriptor = stubDescriptor( ATTRIBUTE_NAME, ATTRIBUTE_VALUE.getClass() );
    preferenceInfo = stubPreferenceInfo( ATTRIBUTE_VALUE, descriptor );
    modifyAdapter = mock( ModifyAdapter.class );
    objectInfo = new PreferenceObjectInfo( preferenceInfo, modifyAdapter );
  }

  @Test
  public void setAttributeValue() {
    objectInfo.setAttributeValue( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

    verify( preferenceInfo ).setAttributeValue( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
  }

  @Test
  public void getAttributeValue() {
    Object actual = objectInfo.getAttributeValue( ATTRIBUTE_NAME );

    assertThat( actual ).isSameAs( ATTRIBUTE_VALUE );
  }

  @Test
  public void getEditableValue() {
    Object actual = objectInfo.getEditableValue();

    assertThat( actual ).isNull();
  }

  @Test
  public void getAttributeInfo() {
    AttributeInfo actual = objectInfo.getAttributeInfo( ATTRIBUTE_NAME );

    assertThat( actual.getAttributeType() ).isSameAs( ATTRIBUTE_VALUE.getClass() );
  }

  @Test
  public void getAttributeInfos() {
    List<AttributeInfo> actual = objectInfo.getAttributeInfos();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( attributeInfo -> attributeInfo.getName().equals( ATTRIBUTE_NAME ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsPreferenceInfoArgument() {
    new PreferenceObjectInfo( null, modifyAdapter );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsModifyArgument() {
    new PreferenceObjectInfo( preferenceInfo, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setAttributeValueWithNullAsAttributeIdArgument() {
    objectInfo.setAttributeValue( null, ATTRIBUTE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getAttributeValueWithNullAsAttributeIdArgument() {
    objectInfo.getAttributeValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getAttributeInfoWithNullAsAttributeIdArgument() {
    objectInfo.getAttributeInfo( null );
  }
}