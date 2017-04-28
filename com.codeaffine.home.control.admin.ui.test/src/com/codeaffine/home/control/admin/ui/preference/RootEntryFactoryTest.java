package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.home.control.admin.ui.test.AttributeDescriptorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetEntry;

public class RootEntryFactoryTest {

  private static final String VALUE = "value";

  private RootEntryFactory factory;

  @Before
  public void setUp() {
    factory = new RootEntryFactory();
  }

  @Test
  public void create() {
    PropertySheetEntry actual = factory.create();

    assertThat( actual ).isNotNull();
  }

  @Test
  public void getPropertySourceOfReturnedRootEntryForValueObjects() {
    PropertySheetEntry root = factory.create();
    Object value = new Object();

    IPropertySource actual = root.getPropertySource( value );

    assertThat( actual )
      .isInstanceOf( ValuePropertySource.class )
      .matches( source -> source.getEditableValue() == value );
  }

  @Test
  public void getPropertySourceOfReturnedRootEntryForPropertySources() {
    PropertySheetEntry root = factory.create();
    IPropertySource expected = mock( IPropertySource.class );

    IPropertySource actual = root.getPropertySource( expected );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void getPropertySourceOfReturnedRootEntryForPreferenceInfo() {
    PropertySheetEntry root = factory.create();
    PreferenceInfo info = stubPreferenceInfo( VALUE, ATTRIBUTE_NAME, String.class );

    IPropertySource actual = root.getPropertySource( info );

    assertThat( actual )
      .isInstanceOf( AttributePropertySource.class )
      .matches( source -> source.getPropertyValue( ATTRIBUTE_NAME ).equals( VALUE ) );
  }

  private static PreferenceInfo stubPreferenceInfo( String value, String attributeName, Class<String> type ) {
    PreferenceInfo result = mock( PreferenceInfo.class );
    PreferenceAttributeDescriptor descriptor = stubAttributeDescriptor( type );
    when( result.getAttributeDescriptor( attributeName ) ).thenReturn( descriptor );
    when( result.getAttributeValue( attributeName ) ).thenReturn( value );
    return result;
  }
}