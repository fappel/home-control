package com.codeaffine.home.control.status.internal.scene;

import static com.codeaffine.home.control.status.SceneSelector.loadScene;
import static com.codeaffine.home.control.status.internal.scene.Messages.*;
import static com.codeaffine.util.ArgumentVerification.*;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.EmptyScene;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.StatusSupplierCore;
import com.codeaffine.home.control.status.supplier.NamedSceneSelection;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier;
import com.codeaffine.home.control.type.StringType;

public class NamedSceneSupplierImpl implements NamedSceneSupplier {

  private final StatusSupplierCore<NamedSceneSelection> statusProviderCore;
  private final Map<String, Class<? extends Scene>> namedScenes;
  private final Context context;

  private final NamedSceneSelectionImpl selection;

  public NamedSceneSupplierImpl(
    Context context, NamedSceneConfiguration configuration, EventBus eventBus, Logger logger )
  {
    verifyNotNull( configuration, "configuration" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( context, "context" );
    verifyNotNull( logger, "logger" );

    this.selection = new NamedSceneSelectionImpl( context );
    this.statusProviderCore = new StatusSupplierCore<>( eventBus, selection.copy(), this, logger );
    this.namedScenes = configure( configuration );
    this.context = context;
  }

  @Observe( "activeScene" )
  public void onActiveSceneItemChange( ChangeEvent<StringItem, StringType> event ) {
    event.getNewStatus().ifPresent( sceneName -> update( sceneName.toString() ) );
  }

  private void update( String sceneName ) {
    statusProviderCore.updateStatus( () -> selectScene( sceneName ), INFO_NAMED_SCENE_SELECTION );
  }

  private NamedSceneSelection selectScene( String sceneName ) {
    verifyCondition( isValidName( sceneName ), ERROR_SCENE_NOT_FOUND, sceneName, namedScenes.keySet() );

    NamedScene nameScene = new NamedScene( context, namedScenes.get( getSceneName( sceneName ) ) );
    Scene scene = loadScene( context, nameScene.getSceneType() );
    if( sceneName.equals( OFF ) ) {
      selection.unselectAll();
    } else if( namedScenes.containsKey( sceneName ) ) {
      selection.select( scene.getScope().orElse( DEFAULT_SCOPE ), nameScene );
    } else {
      selection.unselect( scene.getScope().orElse( DEFAULT_SCOPE ) );
    }
    return selection.copy();
  }

  @Override
  public NamedSceneSelection getStatus() {
    return selection.copy();
  }

  private boolean isValidName( String sceneName ) {
    return namedScenes.containsKey( getSceneName( sceneName ));
  }

  private static String getSceneName( String sceneName ) {
    if( sceneName.endsWith( SCENE_UNSELECT_SUFFIX ) ) {
      return sceneName.substring( 0, sceneName.length() - SCENE_UNSELECT_SUFFIX.length() );
    }
    return sceneName;
  }

  private static Map<String, Class<? extends Scene>> configure( NamedSceneConfiguration configuration ) {
    Map<String, Class<? extends Scene>> result = new HashMap<>();
    configuration.configureNamedScenes( result );
    result.put( OFF, EmptyScene.class );
    return result;
  }
}