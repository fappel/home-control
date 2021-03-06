package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.source.PropertySourceProviderFactory;
import com.codeaffine.home.control.admin.ui.util.viewer.property.PropertySheetEntry;

class RootEntryFactory {

  private final PropertySourceProviderFactory propertySourceProviderFactory;

  RootEntryFactory( PropertySourceProviderFactory propertySourceProviderFactory ) {
    verifyNotNull( propertySourceProviderFactory, "propertySourceProviderFactory" );

    this.propertySourceProviderFactory = propertySourceProviderFactory;
  }

  PropertySheetEntry create( ModifyAdapter modifyAdapter ) {
    verifyNotNull( modifyAdapter, "modifyAdapter" );

    PropertySheetEntry result = new PropertySheetEntry();
    result.setPropertySourceProvider( propertySourceProviderFactory.create( modifyAdapter ) );
    return result;
  }
}