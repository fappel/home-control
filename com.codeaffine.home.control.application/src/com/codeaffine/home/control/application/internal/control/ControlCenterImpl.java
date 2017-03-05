package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.util.ArgumentVerification.*;
import static java.lang.Long.valueOf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.application.control.ControlCenter;
import com.codeaffine.home.control.application.control.ControlCenterOperation;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.application.control.StatusEvent;
import com.codeaffine.home.control.application.sence.FollowUpTimer;
import com.codeaffine.home.control.event.Subscribe;

public class ControlCenterImpl implements ControlCenter, FollowUpTimer {

  private final Map<Class<? extends ControlCenterOperation>, ControlCenterOperation> operations;
  private final SceneSelectionConfigurer sceneSelectionConfigurator;
  private final FollowUpController followUpController;
  private final Context context;

  private SceneSelectorImpl sceneSelector;
  private Scene activeScene;

  public ControlCenterImpl(
    Context context, SceneSelectionConfigurer sceneSelectionConfigurator, SystemExecutor executor )
  {
    verifyNotNull( sceneSelectionConfigurator, "sceneSelectionConfigurator" );
    verifyNotNull( executor, "executor" );
    verifyNotNull( context, "context" );

    this.sceneSelectionConfigurator = sceneSelectionConfigurator;
    this.followUpController = new FollowUpController( executor );
    this.activeScene = new InitialScene();
    this.operations = new HashMap<>();
    this.context = context;
  }

  @Override
  public <T extends ControlCenterOperation> void registerOperation( Class<T> operationType ) {
    verifyNotNull( operationType, "operationType" );

    context.set( operationType, context.create( operationType ) );
    operations.put( operationType, context.get( operationType ) );
  }

  @Override
  public void schedule( long delay, TimeUnit unit, Runnable followUpHandler ) {
    verifyCondition( delay > 0, "Argument delay is expected to be greater than zero but was <%s>.", valueOf( delay ) );
    verifyNotNull( followUpHandler, "followUpHandler" );
    verifyNotNull( unit, "unit" );

    followUpController.schedule( activeScene, delay, unit, followUpHandler, event -> onEvent( event ) );
  }

  @Subscribe
  void onEvent( StatusEvent event ) {
    verifyNotNull( event, "event" );

    ensureSceneSelector();
    followUpController.process( event, activeScene, () -> prepareOperations(), () -> executeOperations( event ) );
  }

  private void ensureSceneSelector() {
    if( sceneSelector == null ) {
      sceneSelector = context.create( SceneSelectorImpl.class );
      sceneSelectionConfigurator.configure( sceneSelector );
      sceneSelector.validate();
    }
  }

  private void prepareOperations() {
    operations.values().forEach( operation -> operation.prepare() );
    Scene nextScene = sceneSelector.select();
    if( !nextScene.equals( activeScene ) ) {
      activeScene.deactivate();
      activeScene = nextScene;
      activeScene.activate();
    }
  }

  private void executeOperations( StatusEvent event ) {
    operations.values().forEach( operation -> operation.executeOn( event ) );
  }
}