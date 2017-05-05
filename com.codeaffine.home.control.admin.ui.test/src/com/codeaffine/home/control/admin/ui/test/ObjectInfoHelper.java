package com.codeaffine.home.control.admin.ui.test;

import static org.mockito.Mockito.*;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

public class ObjectInfoHelper {

  public static final Object ATTRIBUTE_VALUE = "attributeValue";
  public static final String ATTRIBUTE_NAME = "value";
  public static final String BEAN_NAME = "name";

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static AttributeInfo stubAttributeInfo( Class<?> type ) {
    AttributeInfo result = stubAttributeInfo();
    when( result.getAttributeType() ).thenReturn( ( Class )type );
    return result;
  }

  public static AttributeInfo stubAttributeInfo() {
    AttributeInfo result = mock( AttributeInfo.class );
    when( result.getName() ).thenReturn( ATTRIBUTE_NAME );
    when( result.getDisplayName() ).thenReturn( ATTRIBUTE_NAME );
    return result;
  }
}