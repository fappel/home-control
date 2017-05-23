package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.stubAttributeInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.source.PreferenceObjectInfo;

public class AttributeDescriptorSupplierTest {

  private static final String ATTRIBUTE_NAME = "attributeName";

  private AttributeDescriptorSupplier supplier;
  private PreferenceObjectInfo info;

  @Before
  public void setUp() {
    info = mock( PreferenceObjectInfo.class );
    supplier = new AttributeDescriptorSupplier( info );
  }

  @Test
  public void getDescriptorForStringType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, String.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( TextDescriptor.class );
  }

  @Test
  public void getDescriptorForEnumType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, TimeUnit.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( EnumDescriptor.class );
  }

  @Test
  public void getDescriptorForPrimitiveBooleanType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, boolean[].class.getComponentType() );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( BooleanDescriptor.class );
  }

  @Test
  public void getDescriptorForBooleanType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Boolean.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( BooleanDescriptor.class );
  }

  @Test
  public void getDescriptorForSetType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Set.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( CollectionDescriptor.class );
  }

  @Test
  public void getDescriptorForListType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, List.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( CollectionDescriptor.class );
  }

  @Test
  public void getDescriptorForMapType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Map.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( CollectionDescriptor.class );
  }

  @Test
  public void getDescriptorForTypesWithValueOfFactoryMethod() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Integer.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( StandardDescriptor.class );
  }

  @Test
  public void getDescriptorForPreferenceValueType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, TestPreferenceValue.class );


    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( PreferenceValueDescriptor.class );
  }

  @Test
  public void getDescriptorForUnsupportedTypes() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Object.class );

    AttributeDescriptor actual = supplier.getDescriptor( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( ReadOnlyDescriptor.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getDescriptorWithNullAsAttributeNameArgument() {
    supplier.getDescriptor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgument() {
    new AttributeDescriptorSupplier( null );
  }

  private void stubPreferenceInfoWithDescriptor( String attributeName, Class<?> type ) {
    AttributeInfo descriptor = stubAttributeInfo( type );
    when( info.getAttributeInfo( attributeName ) ).thenReturn( descriptor );
  }
}