package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.stream.Stream;

import org.eclipse.swt.widgets.Composite;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetViewer;

public class PreferenceView {

  private final PropertySheetViewer propertySheetViewer;
  private final RootEntryFactory rootEntryFactory;

  public PreferenceView( Composite parent ) {
    this( new PropertySheetViewer( parent ), new RootEntryFactory() );
  }

  PreferenceView( PropertySheetViewer propertySheetViewer, RootEntryFactory rootEntryFactory ) {
    this.propertySheetViewer = propertySheetViewer;
    this.rootEntryFactory = rootEntryFactory;
  }

  public void setInput( PreferenceInfo[] preferenceInfos ) {
    verifyNotNull( preferenceInfos, "preferenceInfos" );

    propertySheetViewer.setRootEntry( rootEntryFactory.create() );
    propertySheetViewer.setInput( adapt( preferenceInfos ) );
  }

  private static Object adapt( PreferenceInfo[] preferenceInfos ) {
    return Stream.of( preferenceInfos )
        .map( info -> new PreferencePropertySource( info ) )
        .toArray( PreferencePropertySource[]::new );
  }
}