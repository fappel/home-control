package com.codeaffine.home.control.admin.ui.control;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class NavigationBarTest {

  private static final Object ACTION_ID = new Object();

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private NavigationBar navigationBar;
  private ActionMap actions;
  private Runnable action;
  private Shell parent;

  @Before
  public void setUp() {
    parent = displayHelper.createShell();
    action = mock( Runnable.class );
    actions = createActionMap( ACTION_ID );
    navigationBar = new NavigationBar( parent, actions );
  }

  @Test
  public void createItemAndSelect() {
    NavigationBarItem itemSpy = mock( NavigationBarItem.class );
    navigationBar.addItem( itemSpy );

    navigationBar.newItem( "label", ACTION_ID );
    navigationBar.selectItem( ACTION_ID );

    verify( action ).run();
    verify( itemSpy ).unselect();
  }

  @Test
  public void getAction() {
    Runnable actual = navigationBar.getAction( ACTION_ID );

    assertThat( actual ).isSameAs( action );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsParentArgument() {
    new NavigationBar( null, actions );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActionsArgument() {
    new NavigationBar( parent, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void newItemWithNullAsLabelArgument() {
    navigationBar.newItem( null, ACTION_ID );
  }

  @Test( expected = IllegalArgumentException.class )
  public void newItemWithNullAsActionIdArgument() {
    navigationBar.newItem( "label", null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void selectItemWithNullAsActionIdArgument() {
    navigationBar.selectItem( null );
  }

  private ActionMap createActionMap( Object actionId ) {
    ActionMap result = new ActionMap();
    result.putAction( actionId, action );
    return result;
  }
}