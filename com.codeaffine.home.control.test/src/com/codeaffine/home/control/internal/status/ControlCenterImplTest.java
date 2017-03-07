package com.codeaffine.home.control.internal.status;

import static com.codeaffine.home.control.test.util.status.MyStatus.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;

public class ControlCenterImplTest {

  private static final String LOG_CONFIGURE_SCENE_SELECTOR = "configure sceneSelector";
  private static final String LOG_PREPARE_OPERATION = "prepare";
  private static final String LOG_ACTIVATE_SCENE_1 = "activate scene 1";
  private static final String LOG_DEACTIVATE_SCENE_1 = "deactivate scene 1";
  private static final String LOG_FOLLOW_UP_SCENE_1 = "follow-up scene 1";
  private static final String LOG_ACTIVATE_SCENE_2 = "activate scene 2";
  private static final String LOG_DEACTIVATE_SCENE_2 = "deactivate scene 2";
  private static final long FOLLOW_UP_DELAY = 10L;
  private static final int DELAY_VALUE_BELOW_LOWER_BOUND = 0;

  private MySceneSelectionConfigurer sceneSelectionConfigurator;
  private ControlCenterImpl controlCenter;
  private MyStatusProvider statusProvider;
  private SystemExecutor executor;
  private TestContext context;
  private List<Object> log;

  static class Scene1 implements Scene {

    private final FollowUpTimer timer;
    private final List<Object> log;

    Scene1( List<Object> log, FollowUpTimer timer )  {
      this.timer = timer;
      this.log = log;
    }

    @Override
    public void activate() {
      log.add( LOG_ACTIVATE_SCENE_1 );
      timer.schedule( FOLLOW_UP_DELAY, SECONDS, () -> log.add( LOG_FOLLOW_UP_SCENE_1 ) );
    }

    @Override
    public void deactivate() {
      log.add( LOG_DEACTIVATE_SCENE_1 );
    }
  }

  static class Scene2 implements Scene {

    private final List<Object> log;

    Scene2( List<Object> log )  {
      this.log = log;
    }

    @Override
    public void activate() {
      log.add( LOG_ACTIVATE_SCENE_2 );
    }

    @Override
    public void deactivate() {
      log.add( LOG_DEACTIVATE_SCENE_2 );
    }
  }

  static class MySceneSelectionConfigurer {

    private boolean useInvalidSelectorConfiguration;
    private final List<Object> log;

    MySceneSelectionConfigurer( List<Object> log ) {
      this.log = log;
    }

    void configureSceneSelection( SceneSelector selector ) {
      log.add( LOG_CONFIGURE_SCENE_SELECTOR );

      if( useInvalidSelectorConfiguration ) {
        selector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE ).thenSelect( Scene1.class );
      } else {
        selector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE ).thenSelect( Scene1.class )
          .otherwiseSelect( Scene2.class );
      }
    }

    void useInvalidSelectorConfiguration() {
      useInvalidSelectorConfiguration = true;
    }
  }

  static class MyOperation implements HomeControlOperation {

    private final List<Object> log;

    public MyOperation( List<Object> log ) {
      this.log = log;
    }

    @Override
    public void prepare() {
      log.add( LOG_PREPARE_OPERATION );
    }

    @Override
    public void executeOn( StatusEvent event ) {
      log.add( event );
    }
  }

  @Before
  public void setUp() {
    log = new ArrayList<>();
    context = new TestContext();
    statusProvider = new MyStatusProvider();
    context.set( MyStatusProvider.class, statusProvider );
    context.set( List.class, log );
    context.set( Logger.class, mock( Logger.class ) );
    executor = mock( SystemExecutor.class );
    context.set( SystemExecutor.class, executor );
    sceneSelectionConfigurator = new MySceneSelectionConfigurer( log );
    controlCenter = new ControlCenterImpl( context );
    controlCenter.registerOperation( MyOperation.class );
    context.set( FollowUpTimer.class, controlCenter );
  }

  @Test
  public void onEvent() {
    statusProvider.setStatus( ONE );
    StatusEvent evt = new StatusEvent( statusProvider );
    sceneSelectionConfigurator.configureSceneSelection( controlCenter );

    controlCenter.onEvent( evt );

    assertThat( log ).containsExactly( LOG_CONFIGURE_SCENE_SELECTOR, LOG_PREPARE_OPERATION, LOG_ACTIVATE_SCENE_1, evt );
  }

  @Test
  public void onEventWithDifferentStatus() {
    statusProvider.setStatus( TWO );
    StatusEvent evt = new StatusEvent( statusProvider );
    sceneSelectionConfigurator.configureSceneSelection( controlCenter );

    controlCenter.onEvent( evt );

    assertThat( log ).containsExactly( LOG_CONFIGURE_SCENE_SELECTOR, LOG_PREPARE_OPERATION, LOG_ACTIVATE_SCENE_2, evt );
  }

  @Test
  public void onEventWithEventSequence() {
    sceneSelectionConfigurator.configureSceneSelection( controlCenter );
    statusProvider.setStatus( ONE );
    StatusEvent firstEvent = new StatusEvent( statusProvider );
    controlCenter.onEvent( firstEvent );
    statusProvider.setStatus( TWO );
    StatusEvent secondEvent = new StatusEvent( statusProvider );
    controlCenter.onEvent( secondEvent );
    statusProvider.setStatus( TWO );
    StatusEvent thirdEvent = new StatusEvent( statusProvider );
    controlCenter.onEvent( thirdEvent );
    statusProvider.setStatus( ONE );
    StatusEvent fourthEvent = new StatusEvent( statusProvider );
    controlCenter.onEvent( fourthEvent );

    assertThat( log )
      .containsExactly( LOG_CONFIGURE_SCENE_SELECTOR,
                        LOG_PREPARE_OPERATION,
                        LOG_ACTIVATE_SCENE_1,
                        firstEvent,
                        LOG_PREPARE_OPERATION,
                        LOG_DEACTIVATE_SCENE_1,
                        LOG_ACTIVATE_SCENE_2,
                        secondEvent,
                        LOG_PREPARE_OPERATION,
                        thirdEvent,
                        LOG_PREPARE_OPERATION,
                        LOG_DEACTIVATE_SCENE_2,
                        LOG_ACTIVATE_SCENE_1,
                        fourthEvent );
  }

  @Test
  public void onEventWithInvalidSceneSelectionConfiguration() {
    sceneSelectionConfigurator.useInvalidSelectorConfiguration();
    sceneSelectionConfigurator.configureSceneSelection( controlCenter );
    statusProvider.setStatus( ONE );

    Throwable actual = thrownBy( () -> controlCenter.onEvent( new StatusEvent( statusProvider ) ) );

    assertThat( actual ).isInstanceOf( IllegalStateException.class );
  }

  @Test
  public void processFollowUp() {
    sceneSelectionConfigurator.configureSceneSelection( controlCenter );
    statusProvider.setStatus( ONE );
    StatusEvent event = new StatusEvent( statusProvider );
    controlCenter.onEvent( event );
    Runnable command = captureFollowUpCallback();

    command.run();

    assertThat( log )
      .containsExactly( LOG_CONFIGURE_SCENE_SELECTOR,
                        LOG_PREPARE_OPERATION,
                        LOG_ACTIVATE_SCENE_1,
                        event,
                        LOG_FOLLOW_UP_SCENE_1,
                        event );
  }

  @Test
  public void processFollowUpIfRelatedSceneHasBeenDeactivated() {
    sceneSelectionConfigurator.configureSceneSelection( controlCenter );
    statusProvider.setStatus( ONE );
    StatusEvent firstEvent = new StatusEvent( statusProvider );
    controlCenter.onEvent( firstEvent );
    Runnable command = captureFollowUpCallback();
    statusProvider.setStatus( TWO );
    StatusEvent secondEvent = new StatusEvent( statusProvider );
    controlCenter.onEvent( secondEvent );

    command.run();

    assertThat( log )
      .containsExactly( LOG_CONFIGURE_SCENE_SELECTOR,
                        LOG_PREPARE_OPERATION,
                        LOG_ACTIVATE_SCENE_1,
                        firstEvent,
                        LOG_PREPARE_OPERATION,
                        LOG_DEACTIVATE_SCENE_1,
                        LOG_ACTIVATE_SCENE_2,
                        secondEvent );
  }

  @Test( expected = IllegalArgumentException.class )
  public void scheduleWithNegativeDelayAsArgument() {
    controlCenter.schedule( DELAY_VALUE_BELOW_LOWER_BOUND, SECONDS, () -> {} );
  }

  @Test( expected = IllegalArgumentException.class )
  public void scheduleWithNullAsTimeUnitArgument() {
    controlCenter.schedule( FOLLOW_UP_DELAY, null, () -> {} );
  }

  @Test( expected = IllegalArgumentException.class )
  public void scheduleWithNullAsFollowUpHandlerArgument() {
    controlCenter.schedule( FOLLOW_UP_DELAY, SECONDS, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void onEventWithNullAsEventArgument() {
    controlCenter.onEvent( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerOperationWithNullAsOperationType() {
    controlCenter.registerOperation( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsContextArgument() {
    new ControlCenterImpl( null );
  }

  private Runnable captureFollowUpCallback() {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( executor ).schedule( captor.capture(), eq( FOLLOW_UP_DELAY ), eq( SECONDS ) );
    return captor.getValue();
  }
}