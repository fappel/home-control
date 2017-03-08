package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.SceneSelectionHelper.newSceneSelection;
import static com.codeaffine.home.control.test.util.status.MyScope.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;
import com.codeaffine.test.util.lang.EqualsTester;

public class FollowUpKeyTest {

  private Map<Scope, Scene> scenes;
  private StatusEvent event;
  private FollowUpKey key;
  private Scene scene;

  @Before
  public void setUp() {
    scene = mock( Scene.class );
    scenes = newSceneSelection( GLOBAL, scene );
    event = new StatusEvent( new MyStatusProvider() );
    key = new FollowUpKey( scenes, event );
  }

  @Test
  public void construction() {
    assertThat( key.getEvent() ).isSameAs( event );
    assertThat( key.getScenes() ).isSameAs( scenes );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<FollowUpKey> tester = EqualsTester.newInstance( key );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertNotEqual( new FollowUpKey( scenes, new StatusEvent( new MyStatusProvider() ) ), key );
    tester.assertNotEqual( new FollowUpKey( newSceneSelection( GLOBAL, mock( Scene.class ) ), event ), key );
    tester.assertNotEqual( new FollowUpKey( newSceneSelection( LOCAL, scene ), event ), key );
    tester.assertNotEqual( new FollowUpKey( newSceneSelection( GLOBAL, mock( Scene.class ) ),
                                            new StatusEvent( new MyStatusProvider() ) ),
                                            key );
    tester.assertEqual( new FollowUpKey( scenes, event ),
                        new FollowUpKey( newSceneSelection( GLOBAL, scene ), event ),
                        key );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSceneArgument() {
    new FollowUpKey( null, event );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventArgument() {
    new FollowUpKey( scenes, null );
  }
}