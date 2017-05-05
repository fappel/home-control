package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertySheetEntry;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetEntry;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetViewer;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.source.PreferencePropertySource;
import com.codeaffine.home.control.admin.ui.preference.source.PropertySourceProviderFactory;

public class PreferenceView {

  private final PropertySheetViewer propertySheetViewer;
  private final RootEntryFactory rootEntryFactory;

  public PreferenceView( Composite parent ) {
    verifyNotNull( parent, "parent" );

    this.propertySheetViewer = new PropertySheetViewer( parent );
    this.rootEntryFactory = new RootEntryFactory( new PropertySourceProviderFactory() );
  }

  public void setInput( PreferenceInfo ... preferenceInfos ) {
    verifyNotNull( preferenceInfos, "preferenceInfos" );

    propertySheetViewer.setRootEntry( createRootEntry( createModifyAdapter( preferenceInfos ) ) );
    propertySheetViewer.setInput( adapt( preferenceInfos ) );
    expandViewerItems();
  }

  public Control getControl() {
    return propertySheetViewer.getControl();
  }

  IPropertySheetEntry getRootEntry() {
    return propertySheetViewer.getRootEntry();
  }

  PropertySheetEntry createRootEntry( ModifyAdapter modifyAdapter ) {
    return rootEntryFactory.create( modifyAdapter );
  }

  private ModifyAdapter createModifyAdapter( PreferenceInfo[] preferenceInfos ) {
    Shell shell = propertySheetViewer.getControl().getShell();
    return new ModifyAdapter( shell, () -> shell.getDisplay().asyncExec( () -> updateViewer( preferenceInfos ) ) );
  }

  private void updateViewer( PreferenceInfo[] preferenceInfos ) {
    propertySheetViewer.getRootEntry().setValues( adapt( preferenceInfos ) );
  }

  private void expandViewerItems() {
    Tree tree = ( Tree )getControl();
    expand( tree, tree.getItems() );
  }

  private static Object[] adapt( PreferenceInfo[] preferenceInfos ) {
    return Stream.of( preferenceInfos )
      .map( info -> new PreferencePropertySource( info ) )
      .toArray( PreferencePropertySource[]::new );
  }

  private void expand( Tree tree, TreeItem[] items ) {
    Stream.of( items ).forEach( item -> {
      item.setExpanded( true );
      Event event = new Event();
      event.item = item;
      tree.notifyListeners( SWT.Expand, event );
      expand( tree, item.getItems() );
    } );
  }
}