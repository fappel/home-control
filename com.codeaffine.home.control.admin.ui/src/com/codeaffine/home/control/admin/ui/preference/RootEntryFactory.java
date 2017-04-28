package com.codeaffine.home.control.admin.ui.preference;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetEntry;

class RootEntryFactory {

  PropertySheetEntry create() {
    PropertySheetEntry result = new PropertySheetEntry();
    result.setPropertySourceProvider( object -> getPropertySource( object ) );
    return result;
  }

  private static IPropertySource getPropertySource( Object object ) {
    if( object instanceof IPropertySource ) {
      return ( IPropertySource )object;
    }
    if( object instanceof PreferenceInfo ) {
      return new AttributePropertySource( ( PreferenceInfo )object );
    }
    return new ValuePropertySource( object );
  }
}