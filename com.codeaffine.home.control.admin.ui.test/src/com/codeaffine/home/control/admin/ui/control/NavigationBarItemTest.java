package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.Theme.*;
import static com.codeaffine.home.control.admin.ui.test.util.SWTEventHelper.trigger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.rap.rwt.RWT.CUSTOM_VARIANT;
import static org.eclipse.swt.SWT.Selection;
import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class NavigationBarItemTest {

  private static final Object ACTION_ID = new Object();
  private static final String LABEL = "label";

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private NavigationBar navigationBar;
  private NavigationBarItem item;
  private Runnable action;

  @Before
  public void setUp() {
    action = mock( Runnable.class );
    navigationBar = stubNavigationBar( action, ACTION_ID, displayHelper.createShell() );
    item = new NavigationBarItem( navigationBar, LABEL, ACTION_ID );
  }

  @Test
  public void initialState() {
    assertThat( item.getActionId() ).isEqualTo( ACTION_ID );
    assertThat( item.getControl().getData( CUSTOM_VARIANT ) ).isEqualTo( CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR );
  }

  @Test
  public void select() {
    item.select();

    verify( action ).run();
    verify( navigationBar ).unselectItems();
    assertThat( item.getControl().getData( CUSTOM_VARIANT ) )
      .isSameAs( CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR_SELECTED );
  }

  @Test
  public void unselect() {
    item.unselect();

    assertThat( item.getControl().getData( CUSTOM_VARIANT ) ).isEqualTo( CUSTOM_VARIANT_NAVIGATION_BAR_SELECTOR );
  }

  @Test
  public void itemSelectionByUserInteraction() {
    trigger( Selection ).on( item.getControl() );

    verify( action ).run();
    verify( navigationBar ).unselectItems();
  }

  private static NavigationBar stubNavigationBar( Runnable action, Object actionId, Composite control ) {
    NavigationBar result = mock( NavigationBar.class );
    when( result.getAction( actionId ) ).thenReturn( action );
    when( result.getControl() ).thenReturn( control );
    return result;
  }
}