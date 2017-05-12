package com.codeaffine.home.control.admin.ui.test;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.List;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.PreferenceInfo;

public class PreferenceInfoHelper {

  public static PreferenceInfo stubPreferenceInfo( String name, String attributeName, Object attributeValue ) {
    PreferenceAttributeDescriptor descriptor = stubDescriptor( attributeName, attributeValue.getClass() );
    return stubPreferenceInfo( name, attributeValue, descriptor );
  }

  public static PreferenceInfo stubPreferenceInfo( Object attributeValue, PreferenceAttributeDescriptor descriptor ) {
    return stubPreferenceInfo( BEAN_NAME, attributeValue, descriptor );
  }

  public static PreferenceInfo stubPreferenceInfo( PreferenceAttributeDescriptor descriptor ) {
    return stubPreferenceInfo( BEAN_NAME, ( Object )null, descriptor );
  }

  public static PreferenceInfo stubPreferenceInfo(
    String name, Object attributeValue, PreferenceAttributeDescriptor descriptor )
  {
    PreferenceInfo result = mock( PreferenceInfo.class );
    stubPreferenceInfoName( result, name );
    when( result.getAttributeDescriptors() ).thenReturn( asList( descriptor ) );
    when( result.getAttributeDescriptor( descriptor.getName() ) ).thenReturn( descriptor );
    when( result.getAttributeValue( descriptor.getName() ) ).thenReturn( attributeValue );
    return result;
  }

  public static void stubPreferenceInfoName( PreferenceInfo info, String name ) {
    when( info.getName() ).thenReturn( name );
  }

  public static PreferenceAttributeDescriptor stubDescriptor() {
    return stubDescriptor( ATTRIBUTE_NAME, ATTRIBUTE_VALUE.getClass() );
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static PreferenceAttributeDescriptor stubDescriptor( String attributeName, Class type ) {
    PreferenceAttributeDescriptor result = mock( PreferenceAttributeDescriptor.class );
    when( result.getDisplayName() ).thenReturn( attributeName );
    when( result.getName() ).thenReturn( attributeName );
    when( result.getAttributeType() ).thenReturn( type );
    return result;
  }

  public static void stubDescriptorWithGenericTypeParametersOfAttributeType(
    PreferenceAttributeDescriptor descriptor, List<Class<?>> genericTypes )
  {
    when( descriptor.getGenericTypeParametersOfAttributeType() ).thenReturn( genericTypes );
  }
}