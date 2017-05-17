package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.home.control.admin.ui.test.PreferenceInfoHelper.*;
import static com.codeaffine.home.control.admin.ui.test.util.DisplayHelper.flushPendingEvents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySheetEntry;

public class PreferenceViewTest {

  private static final String NEW_NAME = "newName";

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private PreferenceView view;

  @Before
  public void setUp() {
    view = spy( new PreferenceView( displayHelper.createShell() ) );
  }

  @Test
  public void setInput() {
    PreferenceInfo preferenceInfo = stubPreferenceInfo( BEAN_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

    view.setInput( preferenceInfo );

    assertThat( getPropertySheetEntries() )
      .hasSize( 1 )
      .allMatch( entry -> entry.getDisplayName().equals( BEAN_NAME ) );
    assertThat( getRootItems() )
      .hasSize( 1 )
      .allMatch( item -> item.getExpanded() );
  }

  @Test
  public void triggerUpdate() {
    PreferenceInfo preferenceInfo = stubPreferenceInfo( BEAN_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
    view.setInput( preferenceInfo );
    ModifyAdapter modifyAdapter = captureModifyAdapter();

    stubPreferenceInfoName( preferenceInfo, NEW_NAME );
    modifyAdapter.triggerUpdate();
    flushPendingEvents();

    assertThat( getPropertySheetEntries() )
      .hasSize( 1 )
      .allMatch( entry -> entry.getDisplayName().equals( NEW_NAME ) );
  }

  @Test
  public void triggerUpdateIfViewControlIsDisposed() {
    PreferenceInfo preferenceInfo = stubPreferenceInfo( BEAN_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
    view.setInput( preferenceInfo );
    ModifyAdapter modifyAdapter = captureModifyAdapter();
    view.getControl().dispose();

    stubPreferenceInfoName( preferenceInfo, NEW_NAME );
    modifyAdapter.triggerUpdate();
    flushPendingEvents();

    assertThat( getPropertySheetEntries() )
      .hasSize( 1 )
      .allMatch( entry -> entry.getDisplayName().equals( BEAN_NAME ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsParentArgument() {
    new PreferenceView( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setInputWithNullAsPreferenceInfosArgumentArray() {
    view.setInput( ( PreferenceInfo[] )null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setInputWithNullAsElementOfPreferenceInfosArgumentArray() {
    view.setInput( new PreferenceInfo[ 1 ] );
  }

  private IPropertySheetEntry[] getPropertySheetEntries() {
    IPropertySheetEntry rootEntry = view.getRootEntry();
    return rootEntry.getChildEntries();
  }

  private TreeItem[] getRootItems() {
    Tree tree = ( Tree )view.getControl();
    return tree.getItems();
  }

  private ModifyAdapter captureModifyAdapter() {
    ArgumentCaptor<ModifyAdapter> captor = forClass( ModifyAdapter.class );
    verify( view ).createRootEntry( captor.capture() );
    return captor.getValue();
  }
}