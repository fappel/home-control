package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.ATTRIBUTE_NAME;
import static com.codeaffine.home.control.admin.ui.test.PreferenceInfoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;

public class AttributePropertySourceTest {

  private static final String ATTRIBUTE_VALUE_LABEL = "4";

  private AttributePropertySource propertySource;
  private PreferenceInfo info;

  @Before
  public void setUp() {
    info = stubPreferenceInfo( stubDescriptor( ATTRIBUTE_NAME, Integer.class ) );
    ModifyAdapter modifyAdapter = mock( ModifyAdapter.class );
    propertySource = new AttributePropertySource( new PreferenceObjectInfo( info, modifyAdapter ) );
  }

  @Test
  public void setPropertyValue() {
    propertySource.setPropertyValue( ATTRIBUTE_NAME, ATTRIBUTE_VALUE_LABEL );

    verify( info ).setAttributeValue( ATTRIBUTE_NAME, Integer.valueOf( ATTRIBUTE_VALUE_LABEL ) );
  }

  @Test
  public void getPropertyValue() {
    when( info.getAttributeValue( ATTRIBUTE_NAME ) ).thenReturn( Integer.valueOf( ATTRIBUTE_VALUE_LABEL )  );

    Object propertyValue = propertySource.getPropertyValue( ATTRIBUTE_NAME );

    assertThat( propertyValue ).isEqualTo( ATTRIBUTE_VALUE_LABEL );
  }

  @Test
  public void getPropertyDescriptors() {
    IPropertyDescriptor[] actual = propertySource.getPropertyDescriptors();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) );
  }

  @Test
  public void isPropertySet() {
    boolean actual = propertySource.isPropertySet( ATTRIBUTE_NAME );

    assertThat( actual ).isTrue();
  }

  @Test
  public void getEditableValue() {
    Object actual = propertySource.getEditableValue();

    assertThat( actual ).isNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void setPropertyValueWithNullAsIdArgument() {
    propertySource.setPropertyValue( null, ATTRIBUTE_VALUE_LABEL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setPropertyValueWithIdArgumentOfInvalidType() {
    propertySource.setPropertyValue( new Object(), ATTRIBUTE_VALUE_LABEL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getPropertyValueWithNullAsIdArgument() {
    propertySource.getPropertyValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getPropertyValueWithIdArgumentOfInvalidType() {
    propertySource.getPropertyValue( new Object() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isPropertySetWithNullAsIdArgument() {
    propertySource.getPropertyValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgument() {
    new AttributePropertySource( null );
  }
}