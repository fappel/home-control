package com.codeaffine.home.control.admin.ui.model;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
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
  public void initialState() {
    assertThat( actionMap.isEmpty() ).isTrue();
  }

  @Test
  public void putAction() {
    actionMap.putAction( ACTION_ID, ACTION );
    Runnable actual = actionMap.getAction( ACTION_ID );

    assertThat( actual ).isSameAs( ACTION );
    assertThat( actionMap.isEmpty() ).isFalse();
  }

  @Test
  public void removeAction() {
    actionMap.putAction( ACTION_ID, ACTION );

    actionMap.removeAction( ACTION_ID );
    Throwable actual = thrownBy( () -> actionMap.getAction( ACTION_ID ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
    assertThat( actionMap.isEmpty() ).isTrue();
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

  @Test( expected = IllegalArgumentException.class )
  public void removeActionWithNullAsActionIdArgument() {
    actionMap.removeAction( null );
  }
}