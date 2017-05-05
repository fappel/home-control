package com.codeaffine.home.control.admin.ui.preference.source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.source.PreferencePropertySource;

public class PreferencePropertySourceTest {

  private static final String NAME = "Name";

  private PreferencePropertySource propertySource;
  private PreferenceInfo info;

  @Before
  public void setUp() {
    info = stubInfo( NAME );
    propertySource = new PreferencePropertySource( info );
  }

  @Test
  public void getPropertyDescriptors() {
    IPropertyDescriptor[] actual = propertySource.getPropertyDescriptors();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( descriptor -> descriptor.getDisplayName().equals( NAME ) )
      .allMatch( descriptor -> descriptor.getId().equals( NAME ) );
  }

  @Test
  public void getPropertyValue() {
    Object actual = propertySource.getPropertyValue( NAME );

    assertThat( actual ).isSameAs( info );
  }

  @Test
  public void isPropertySet() {
    boolean actual = propertySource.isPropertySet( NAME );

    assertThat( actual ).isTrue();
  }

  @Test( expected = IllegalArgumentException.class )
  public void getPropertyValueWithNullAsIdArgument() {
    propertySource.getPropertyValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isPropertySetWithNullAsIdArgument() {
    propertySource.isPropertySet( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgument() {
    new PreferencePropertySource( null );
  }

  private static PreferenceInfo stubInfo( String name ) {
    PreferenceInfo info = mock( PreferenceInfo.class );
    when( info.getName() ).thenReturn( name );
    return info;
  }
}
