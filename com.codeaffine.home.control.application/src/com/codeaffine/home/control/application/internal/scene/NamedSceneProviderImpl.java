package com.codeaffine.home.control.application.internal.scene;

import static com.codeaffine.home.control.application.internal.scene.Messages.*;
import static com.codeaffine.util.ArgumentVerification.*;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.application.status.NamedScene;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusProviderCore;
import com.codeaffine.home.control.type.StringType;

public class NamedSceneProviderImpl implements NamedSceneProvider {

  private final StatusProviderCore<NamedScene> statusProviderCore;
  private final Map<String, Class<? extends Scene>> namedScenes;
  private final Context context;

  private NamedScene nameScene;

  NamedSceneProviderImpl( Context context, NamedSceneConfiguration configuration, EventBus eventBus, Logger logger ) {
    verifyNotNull( configuration, "configuration" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( context, "context" );
    verifyNotNull( logger, "logger" );

    this.nameScene = new NamedScene( context, NamedSceneDefaultSelection.class );
    this.statusProviderCore = new StatusProviderCore<>( eventBus, nameScene, this, logger );
    this.namedScenes = configure( configuration );
    this.context = context;
  }

  @Observe( "activeScene" )
  void onActiveSceneItemChange( ChangeEvent<StringItem, StringType> event ) {
    event.getNewStatus().ifPresent( sceneName -> update( sceneName.toString() ) );
  }

  private void update( String sceneName ) {
    statusProviderCore.updateStatus( () -> selectScene( sceneName ), INFO_NAMED_SCENE_SELECTION );
  }

  private NamedScene selectScene( String sceneName ) {
    verifyCondition( namedScenes.containsKey( sceneName ), ERROR_SCENE_NOT_FOUND, sceneName, namedScenes.keySet() );

    nameScene = new NamedScene( context, namedScenes.get( sceneName ) );
    return nameScene;
  }

  @Override
  public NamedScene getStatus() {
    return nameScene;
  }

  private static Map<String, Class<? extends Scene>> configure( NamedSceneConfiguration configuration ) {
    Map<String, Class<? extends Scene>> result = new HashMap<>();
    configuration.configureNamedScenes( result );
    result.put( OFF, NamedSceneDefaultSelection.class );
    return result;
  }
}