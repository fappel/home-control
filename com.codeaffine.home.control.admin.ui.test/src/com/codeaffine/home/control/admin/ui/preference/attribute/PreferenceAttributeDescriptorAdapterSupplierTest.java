package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.test.AttributeDescriptorHelper.stubAttributeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.PreferenceAttributeDescriptorAdapter;

public class PreferenceAttributeDescriptorAdapterSupplierTest {

  private static final String ATTRIBUTE_NAME = "attributeName";

  private PreferenceAttributeDescriptorAdapterSupplier supplier;
  private PreferenceInfo info;

  @Before
  public void setUp() {
    info = mock( PreferenceInfo.class );
    supplier = new PreferenceAttributeDescriptorAdapterSupplier( info );
  }

  @Test
  public void getAttributeDescriptorAdapterForStringType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, String.class );

    PreferenceAttributeDescriptorAdapter actual = supplier.getAttributeDescriptorAdapter( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( TextAdapter.class );
  }

  @Test
  public void getAttributeDescriptorAdapterForEnumType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, TimeUnit.class );

    PreferenceAttributeDescriptorAdapter actual = supplier.getAttributeDescriptorAdapter( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( EnumAdapter.class );
  }

  @Test
  public void getAttributeDescriptorAdapterForPrimitiveBooleanType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, boolean[].class.getComponentType() );

    PreferenceAttributeDescriptorAdapter actual = supplier.getAttributeDescriptorAdapter( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( BooleanAdapter.class );
  }

  @Test
  public void getAttributeDescriptorAdapterForBooleanType() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Boolean.class );

    PreferenceAttributeDescriptorAdapter actual = supplier.getAttributeDescriptorAdapter( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( BooleanAdapter.class );
  }

  @Test
  public void getAttributeDescriptorAdapterForTypesWithValueOfFactoryMethod() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Integer.class );

    PreferenceAttributeDescriptorAdapter actual = supplier.getAttributeDescriptorAdapter( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( StandardAdapter.class );
  }

  @Test
  public void getAttributeDescriptorAdapterForUnsupportedTypes() {
    stubPreferenceInfoWithDescriptor( ATTRIBUTE_NAME, Object.class );

    PreferenceAttributeDescriptorAdapter actual = supplier.getAttributeDescriptorAdapter( ATTRIBUTE_NAME );

    assertThat( actual ).isInstanceOf( ReadOnlyAdapter.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getAttributeDescriptorWithNullAsAttributeNameArgument() {
    supplier.getAttributeDescriptorAdapter( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgument() {
    new PreferenceAttributeDescriptorAdapterSupplier( null );
  }

  private void stubPreferenceInfoWithDescriptor( String attributeName, Class<?> type ) {
    PreferenceAttributeDescriptor descriptor = stubAttributeDescriptor( type );
    when( info.getAttributeDescriptor( attributeName ) ).thenReturn( descriptor );
  }
}