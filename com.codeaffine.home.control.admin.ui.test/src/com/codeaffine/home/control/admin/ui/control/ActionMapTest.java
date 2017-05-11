package com.codeaffine.home.control.admin.ui.control;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ActionMapTest {

  private static final Object ACTION_ID = new Object();
  private static final Runnable ACTION = () -> {};

  private ActionMap actionMap;

  @Before
  public void setUp() {
    actionMap = new ActionMap();
  }

  @Test
  public void putAction() {
    actionMap.putAction( ACTION_ID, ACTION );
    Runnable actual = actionMap.getAction( ACTION_ID );

    assertThat( actual ).isSameAs( ACTION );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getActionWithNullAsActionIdArgument() {
    actionMap.getAction( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getActionWithActionIdThatDoesNotExist() {
    actionMap.getAction( ACTION_ID );
  }

  @Test( expected = IllegalArgumentException.class )
  public void putActionWithNullAsActionIdArgument() {
    actionMap.putAction( null, ACTION );
  }

  @Test( expected = IllegalArgumentException.class )
  public void putActionWithNullAsActionArgument() {
    actionMap.putAction( ACTION_ID, null );
  }
}