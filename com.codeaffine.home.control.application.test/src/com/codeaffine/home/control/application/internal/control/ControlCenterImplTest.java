package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.Status.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.control.ControlCenter.SceneSelectionConfigurer;
import com.codeaffine.home.control.application.control.ControlCenterOperation;
import com.codeaffine.home.control.application.control.Event;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.control.SceneSelector;
import com.codeaffine.home.control.test.util.context.TestContext;

public class ControlCenterImplTest {

  private static final String LOG_CONFIGURE_SCENE_SELECTOR = "configure sceneSelector";
  private static final String LOG_PREPARE_OPERATION = "prepare";
  private static final String LOG_APPLY_SCENE_1 = "apply scene 1";
  private static final String LOG_APPLY_SCENE_2 = "apply scene 2";

  private MySceneSelectionConfigurer sceneSelectionConfigurator;
  private ControlCenterImpl controlCenter;
  private MyStatusProvider statusProvider;
  private TestContext context;
  private List<Object> log;

  static class Scene1 implements Scene {

    private final List<Object> log;

    Scene1( List<Object> log )  {
      this.log = log;
    }

    @Override
    public void apply() {
      log.add( LOG_APPLY_SCENE_1 );
    }
  }

  static class Scene2 implements Scene {

    private final List<Object> log;

    Scene2( List<Object> log )  {
      this.log = log;
    }

    @Override
    public void apply() {
      log.add( LOG_APPLY_SCENE_2 );
    }
  }

  static class MySceneSelectionConfigurer implements SceneSelectionConfigurer {

    private boolean useInvalidSelectorConfiguration;
    private final List<Object> log;

    MySceneSelectionConfigurer( List<Object> log ) {
      this.log = log;
    }

    @Override
    public void configure( SceneSelector selector ) {
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

  static class MyOperation implements ControlCenterOperation {

    private final List<Object> log;

    public MyOperation( List<Object> log ) {
      this.log = log;
    }

    @Override
    public void prepare() {
      log.add( LOG_PREPARE_OPERATION );
    }

    @Override
    public void executeOn( Event event ) {
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
    sceneSelectionConfigurator = new MySceneSelectionConfigurer( log );
    controlCenter = new ControlCenterImpl( context, sceneSelectionConfigurator );
    controlCenter.registerOperation( MyOperation.class );
  }

  @Test
  public void onEvent() {
    statusProvider.setStatus( ONE );
    Event event = new Event( statusProvider );

    controlCenter.onEvent( event );

    assertThat( log ).containsExactly( LOG_CONFIGURE_SCENE_SELECTOR, LOG_PREPARE_OPERATION, LOG_APPLY_SCENE_1, event );
  }

  @Test
  public void onEventWithDifferentStatus() {
    statusProvider.setStatus( TWO );
    Event event = new Event( statusProvider );

    controlCenter.onEvent( event );

    assertThat( log ).containsExactly( LOG_CONFIGURE_SCENE_SELECTOR, LOG_PREPARE_OPERATION, LOG_APPLY_SCENE_2, event );
  }

  @Test
  public void onEventWithEventSequence() {
    statusProvider.setStatus( ONE );
    Event firstEvent = new Event( statusProvider );
    controlCenter.onEvent( firstEvent );
    statusProvider.setStatus( TWO );
    Event secondEvent = new Event( statusProvider );
    controlCenter.onEvent( secondEvent );

    assertThat( log )
      .containsExactly( LOG_CONFIGURE_SCENE_SELECTOR,
                        LOG_PREPARE_OPERATION,
                        LOG_APPLY_SCENE_1,
                        firstEvent,
                        LOG_PREPARE_OPERATION,
                        LOG_APPLY_SCENE_2,
                        secondEvent );
  }

  @Test
  public void onEventWithInvalidSceneSelectionConfiguration() {
    sceneSelectionConfigurator.useInvalidSelectorConfiguration();
    statusProvider.setStatus( ONE );

    Throwable actual = thrownBy( () -> controlCenter.onEvent( new Event( statusProvider ) ) );

    assertThat( actual ).isInstanceOf( IllegalStateException.class );
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
    new ControlCenterImpl( null, new MySceneSelectionConfigurer( null ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSceneSelectionConfigurerArgument() {
    new ControlCenterImpl( new TestContext(), null );
  }
}