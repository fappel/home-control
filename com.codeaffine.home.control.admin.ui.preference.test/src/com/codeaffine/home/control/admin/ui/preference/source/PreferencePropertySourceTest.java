package com.codeaffine.home.control.admin.ui.preference.source;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;

public class PreferencePropertySourceTest {

  private static final String NAME_1 = "Name1";
  private static final String NAME_2 = "Name2";

  private PreferencePropertySource propertySource;
  private List<PreferenceInfo> infos;
  private PreferenceInfo info1;
  private PreferenceInfo info2;

  @Before
  public void setUp() {
    info1 = stubInfo( NAME_1 );
    info2 = stubInfo( NAME_2 );
    infos = asList( info1, info2 );
    propertySource = new PreferencePropertySource( infos.toArray( new PreferenceInfo[ infos.size() ] ) );
  }

  @Test
  public void getPropertyDescriptors() {
    IPropertyDescriptor[] actual = propertySource.getPropertyDescriptors();

    assertThat( actual ).hasSize( infos.size() );
    assertThat( actual[ 0 ] ).matches( descriptor -> descriptor.getId().equals( NAME_1 ) );
    assertThat( actual[ 0 ] ).matches( descriptor -> descriptor.getDisplayName().equals( NAME_1 ) );
    assertThat( actual[ 1 ] ).matches( descriptor -> descriptor.getId().equals( NAME_2 ) );
    assertThat( actual[ 1 ] ).matches( descriptor -> descriptor.getDisplayName().equals( NAME_2 ) );
  }

  @Test
  public void getPropertyValue() {
    assertThat( propertySource.getPropertyValue( NAME_1 ) ).isSameAs( info1 );
    assertThat( propertySource.getPropertyValue( NAME_2 ) ).isSameAs( info2 );
  }

  @Test
  public void isPropertySet() {
    assertThat( propertySource.isPropertySet( NAME_1 ) ).isTrue();
    assertThat( propertySource.isPropertySet( NAME_2 ) ).isTrue();
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
  public void constructWithNullAsInfoArgumentArray() {
    new PreferencePropertySource( ( PreferenceInfo[] ) null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInfoArgumentArrayElement() {
    new PreferencePropertySource( new PreferenceInfo[ 1 ] );
  }

  private static PreferenceInfo stubInfo( String name ) {
    PreferenceInfo info = mock( PreferenceInfo.class );
    when( info.getName() ).thenReturn( name );
    return info;
  }
}
