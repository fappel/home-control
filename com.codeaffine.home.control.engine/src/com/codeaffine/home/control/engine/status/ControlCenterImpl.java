package com.codeaffine.home.control.engine.status;

import static com.codeaffine.util.ArgumentVerification.*;
import static java.lang.Long.valueOf;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.status.ControlCenter;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusProvider;

public class ControlCenterImpl implements ControlCenter, FollowUpTimer, SceneSelector {

  private final Map<Class<? extends HomeControlOperation>, HomeControlOperation> operations;
  private final FollowUpController followUpController;
  private final SceneSelectorImpl sceneSelector;
  private final Context context;

  private final Map<Scope, Scene> activeScenes;

  public ControlCenterImpl( Context context ) {
    verifyNotNull( context, "context" );

    this.followUpController = context.create( FollowUpController.class );
    this.sceneSelector = context.create( SceneSelectorImpl.class );
    this.activeScenes = new HashMap<>();
    this.operations = new HashMap<>();
    this.context = context;
  }

  @Override
  public <T extends HomeControlOperation> void registerOperation( Class<T> operationType ) {
    verifyNotNull( operationType, "operationType" );

    context.set( operationType, context.create( operationType ) );
    operations.put( operationType, context.get( operationType ) );
  }

  @Override
  public <S> NodeCondition<S> whenStatusOf( Scope scope, Class<? extends StatusProvider<S>> statusProviderType ) {
    return sceneSelector.whenStatusOf( scope, statusProviderType );
  }

  @Override
  public void schedule( long delay, TimeUnit unit, Runnable followUpHandler ) {
    verifyCondition( delay > 0, "Argument delay is expected to be greater than zero but was <%s>.", valueOf( delay ) );
    verifyNotNull( followUpHandler, "followUpHandler" );
    verifyNotNull( unit, "unit" );

    followUpController.schedule( activeScenes, delay, unit, followUpHandler, event -> onEvent( event ) );
  }

  @Subscribe
  public void onEvent( StatusEvent event ) {
    verifyNotNull( event, "event" );

    sceneSelector.validate();
    followUpController.process( event, activeScenes, () -> prepareOperations(), () -> executeOperations( event ) );
  }

  private void prepareOperations() {
    operations.values().forEach( operation -> operation.reset() );
    sortScopesReversely( activeScenes ).forEach( scope -> activeScenes.get( scope ).close() );
    activeScenes.clear();
    activeScenes.putAll( sceneSelector.select() );
    sortScopes( activeScenes ).forEach( scope -> activeScenes.get( scope ).prepare() );
  }

  private static List<Scope> sortScopesReversely( Map<Scope, Scene> scenes ) {
    List<Scope> result = sortScopes( scenes );
    reverse( result );
    return result;
  }

  private static List<Scope> sortScopes( Map<Scope, Scene> scenes ) {
    return scenes.keySet().stream().sorted().collect( toList() );
  }

  private void executeOperations( StatusEvent event ) {
    operations.values().forEach( operation -> operation.executeOn( event ) );
  }
}