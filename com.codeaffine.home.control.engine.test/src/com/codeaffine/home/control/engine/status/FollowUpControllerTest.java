package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.Messages.ERROR_SCHEDULE_CALLED_OUTSIDE_OF_SCENE_ACTIVATION;
import static com.codeaffine.home.control.engine.status.SceneSelectionHelper.newSceneSelection;
import static com.codeaffine.home.control.test.util.status.MyScope.GLOBAL;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;

public class FollowUpControllerTest {

  private static final Map<Scope, Scene> ACTIVE_SCENE = newSceneSelection( GLOBAL, mock( Scene.class ) );
  private static final StatusEvent STATUS_EVENT = new StatusEvent( new MyStatusProvider() );
  private static final long DELAY = 10L;

  private SystemExecutor executor;
  private FollowUpController controller;

  @Before
  public void setUp() {
    executor = mock( SystemExecutor.class );
    controller = new FollowUpController( executor );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void schedule() {
    Consumer<StatusEvent> processCallback = mock( Consumer.class );

    Runnable onSceneActivation = () -> controller.schedule( ACTIVE_SCENE, DELAY, SECONDS, () -> {}, processCallback );
    controller.process( STATUS_EVENT, ACTIVE_SCENE, onSceneActivation, () -> {} );

    captureCommand( DELAY, SECONDS ).run();
    verify( processCallback ).accept( STATUS_EVENT );
  }

  @Test
  public void scheduleMultipleFollowUps() {
    Runnable onActivation = () -> controller.schedule( ACTIVE_SCENE, DELAY, SECONDS, () -> {}, event -> {} );
    controller.process( STATUS_EVENT, ACTIVE_SCENE, () -> { onActivation.run(); onActivation.run(); }, () -> {} );

    verify( executor, times( 2 ) ).schedule( any( Runnable.class ), eq( DELAY ), eq( SECONDS ) );
  }

  @Test
  public void scheduleOutsideOfProcessCall() {
    Throwable actual = thrownBy( () -> controller.schedule( ACTIVE_SCENE, DELAY, SECONDS, () -> {}, event -> {} ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_SCHEDULE_CALLED_OUTSIDE_OF_SCENE_ACTIVATION );
  }

  @Test
  public void process() {
    Runnable followUp = mock( Runnable.class );
    Runnable preparations = mock( Runnable.class );
    Runnable actions = mock( Runnable.class );
    Consumer<StatusEvent> callback = evt -> controller.process( STATUS_EVENT, ACTIVE_SCENE, preparations, actions );
    Runnable onSceneActivation = () -> controller.schedule( ACTIVE_SCENE, DELAY, SECONDS, followUp, callback );
    controller.process( STATUS_EVENT, ACTIVE_SCENE, onSceneActivation, () -> {} );
    Runnable command = captureCommand( DELAY, SECONDS );

    command.run();

    InOrder order = inOrder( followUp, preparations, actions );
    order.verify( followUp ).run();
    order.verify( actions ).run();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void processIfFollowUpHasBeenConsumed() {
    Runnable followUp = mock( Runnable.class );
    Runnable preparations = mock( Runnable.class );
    Runnable actions = mock( Runnable.class );
    Consumer<StatusEvent> callback = event -> controller.process( STATUS_EVENT, ACTIVE_SCENE, preparations, actions );
    Runnable onSceneActivation = () -> controller.schedule( ACTIVE_SCENE, DELAY, SECONDS, followUp, callback );
    controller.process( STATUS_EVENT, ACTIVE_SCENE, onSceneActivation, () -> {} );
    Runnable command = captureCommand( DELAY, SECONDS );

    command.run();
    command.run();

    InOrder order = inOrder( followUp, preparations, actions );
    order.verify( followUp ).run();
    order.verify( actions ).run();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void processWithoutSchedule() {
    Runnable preparations = mock( Runnable.class );
    Runnable actions = mock( Runnable.class );
    controller.process( STATUS_EVENT, ACTIVE_SCENE, preparations, actions );

    InOrder order = inOrder( preparations, actions );
    order.verify( preparations ).run();
    order.verify( actions ).run();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void processWithProblem() {
    RuntimeException expected = new RuntimeException();
    Runnable troubleMaker = () -> { throw expected; };

    Throwable actual = thrownBy( () -> controller.process( STATUS_EVENT, ACTIVE_SCENE, troubleMaker, () -> {} ) );
    Throwable expectedProblemOnOutOfSceneActivation
      = thrownBy( () -> controller.schedule( ACTIVE_SCENE, DELAY, SECONDS, () -> {}, event -> {} ) );

    assertThat( actual ).isSameAs( expected );
    assertThat( expectedProblemOnOutOfSceneActivation )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( ERROR_SCHEDULE_CALLED_OUTSIDE_OF_SCENE_ACTIVATION );
  }

  @Test
  public void processWithProblemInFollowUp() {
    RuntimeException expected = new RuntimeException();
    Runnable followUp = () -> { throw expected; };
    Runnable preparations = mock( Runnable.class );
    Runnable actions = mock( Runnable.class );
    Consumer<StatusEvent> callback = event -> controller.process( STATUS_EVENT, ACTIVE_SCENE, preparations, actions );
    Runnable onSceneActivation = () -> controller.schedule( ACTIVE_SCENE, DELAY, SECONDS, followUp, callback );
    controller.process( STATUS_EVENT, ACTIVE_SCENE, onSceneActivation, () -> {} );
    Runnable command = captureCommand( DELAY, SECONDS );
    controller.process( STATUS_EVENT, ACTIVE_SCENE, preparations, actions );

    Throwable actual = thrownBy( () -> command.run() );

    assertThat( actual ).isSameAs( expected );
    InOrder order = inOrder( preparations, actions );
    order.verify( preparations ).run();
    order.verify( actions ).run();
    order.verifyNoMoreInteractions();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsExecutor() {
    new FollowUpController( null );
  }

  private Runnable captureCommand( long delay, TimeUnit timeUnit ) {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( executor ).schedule( captor.capture(), eq( delay ), eq( timeUnit ) );
    return captor.getValue();
  }
}