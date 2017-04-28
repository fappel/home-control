package com.codeaffine.home.control.admin.ui.test;

import static org.mockito.Mockito.*;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;

public class AttributeDescriptorHelper {

  public static final String ATTRIBUTE_NAME = "value";

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static PreferenceAttributeDescriptor stubAttributeDescriptor( Class<?> type ) {
    PreferenceAttributeDescriptor result = stubAttributeDescriptor();
    when( result.getAttributeType() ).thenReturn( ( Class )type );
    return result;
  }

  public static PreferenceAttributeDescriptor stubAttributeDescriptor() {
    PreferenceAttributeDescriptor descriptor = mock( PreferenceAttributeDescriptor.class );
    when( descriptor.getName() ).thenReturn( ATTRIBUTE_NAME );
    when( descriptor.getDisplayName() ).thenReturn( ATTRIBUTE_NAME );
    return descriptor;
  }
}