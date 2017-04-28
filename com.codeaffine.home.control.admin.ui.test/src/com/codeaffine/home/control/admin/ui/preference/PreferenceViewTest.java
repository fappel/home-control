package com.codeaffine.home.control.admin.ui.preference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetEntry;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetViewer;

public class PreferenceViewTest {

  private static final String NAME = "name";

  private PropertySheetViewer viewer;
  private PropertySheetEntry root;
  private PreferenceView view;

  @Before
  public void setUp() {
    viewer = mock( PropertySheetViewer.class );
    root = new PropertySheetEntry();
    view = new PreferenceView( viewer, stubRootEntryFactory( root ) );
  }

  @Test
  public void setInput() {
    ArgumentCaptor<PreferencePropertySource[]> captor = forClass( PreferencePropertySource[].class );
    PreferenceInfo expected = stubPreferenceInfo( NAME );

    view.setInput( new PreferenceInfo[] { expected } );

    InOrder order = inOrder( viewer );
    order.verify( viewer ).setRootEntry( root );
    order.verify( viewer ).setInput( captor.capture() );
    order.verifyNoMoreInteractions();
    assertThat( captor.getValue() )
      .hasSize( 1 )
      .allMatch( propertySource -> propertySource.getPropertyDescriptors().length == 1 )
      .allMatch( propertySource -> propertySource.getPropertyDescriptors()[ 0 ].getDisplayName().equals( NAME ) )
      .allMatch( propertySource -> propertySource.getPropertyDescriptors()[ 0 ].getId().equals( NAME ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setInputWithNullAsPreferenceInfosArgumentArray() {
    view.setInput( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setInputWithNullAsElementOfPreferenceInfosArgumentArray() {
    view.setInput( new PreferenceInfo[ 1 ] );
  }

  private static PreferenceInfo stubPreferenceInfo( String name ) {
    PreferenceInfo result = mock( PreferenceInfo.class );
    when( result.getName() ).thenReturn( name );
    return result;
  }

  private static RootEntryFactory stubRootEntryFactory( PropertySheetEntry root ) {
    RootEntryFactory result = mock( RootEntryFactory.class );
    when( result.create() ).thenReturn( root );
    return result;
  }
}