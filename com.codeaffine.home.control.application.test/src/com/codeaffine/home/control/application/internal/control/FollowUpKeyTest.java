package com.codeaffine.home.control.application.internal.control;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.control.StatusEvent;
import com.codeaffine.test.util.lang.EqualsTester;

public class FollowUpKeyTest {

  private StatusEvent event;
  private FollowUpKey key;
  private Scene scene;

  @Before
  public void setUp() {
    scene = mock( Scene.class );
    event = new StatusEvent( new MyStatusProvider() );
    key = new FollowUpKey( scene, event );
  }

  @Test
  public void construction() {
    assertThat( key.getEvent() ).isSameAs( event );
    assertThat( key.getScene() ).isSameAs( scene );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<FollowUpKey> tester = EqualsTester.newInstance( key );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new FollowUpKey( scene, event ), key );
    tester.assertNotEqual( new FollowUpKey( scene, new StatusEvent( new MyStatusProvider() ) ), key );
    tester.assertNotEqual( new FollowUpKey( mock( Scene.class ), event ), key );
    tester.assertNotEqual( new FollowUpKey( mock( Scene.class ), new StatusEvent( new MyStatusProvider() ) ), key );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSceneArgument() {
    new FollowUpKey( null, event );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventArgument() {
    new FollowUpKey( scene, null );
  }
}