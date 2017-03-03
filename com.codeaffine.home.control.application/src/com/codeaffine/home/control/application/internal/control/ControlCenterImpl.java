package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.application.control.ControlCenter;
import com.codeaffine.home.control.application.control.ControlCenterOperation;
import com.codeaffine.home.control.application.control.StatusEvent;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.event.Subscribe;

public class ControlCenterImpl implements ControlCenter {

  private final Map<Class<? extends ControlCenterOperation>, ControlCenterOperation> operations;
  private final SceneSelectionConfigurer sceneSelectionConfigurator;
  private final Context context;

  private SceneSelectorImpl sceneSelector;

  public ControlCenterImpl( Context context, SceneSelectionConfigurer sceneSelectionConfigurator ) {
    verifyNotNull( sceneSelectionConfigurator, "sceneSelectionConfigurator" );
    verifyNotNull( context, "context" );

    this.sceneSelectionConfigurator = sceneSelectionConfigurator;
    this.operations = new HashMap<>();
    this.context = context;
  }

  @Override
  public <T extends ControlCenterOperation> void registerOperation( Class<T> operationType ) {
    verifyNotNull( operationType, "operationType" );

    context.set( operationType, context.create( operationType ) );
    operations.put( operationType, context.get( operationType ) );
  }

  @Subscribe
  void onEvent( StatusEvent event ) {
    verifyNotNull( event, "event" );

    ensureSceneSelector();
    operations.values().forEach( operation -> operation.prepare() );
    selectScene().apply();
    operations.values().forEach( operation -> operation.executeOn( event ) );
  }

  private void ensureSceneSelector() {
    if( sceneSelector == null ) {
      sceneSelector = new SceneSelectorImpl( context );
      sceneSelectionConfigurator.configure( sceneSelector );
      sceneSelector.validate();
    }
  }

  private Scene selectScene() {
    return sceneSelector.getDecision();
  }
}