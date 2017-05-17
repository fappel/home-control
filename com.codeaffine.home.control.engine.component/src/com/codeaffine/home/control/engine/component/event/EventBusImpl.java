package com.codeaffine.home.control.engine.component.event;

import static com.codeaffine.home.control.util.reflection.ReflectionUtil.getAnnotatedMethods;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.Context.Disposable;
import com.codeaffine.home.control.engine.component.util.TypeUnloadTracker;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Subscribe;

public class EventBusImpl implements EventBus, Disposable {

  private final Map<Object, Collection<ObserverAdapter>> observers;
  private final com.google.common.eventbus.EventBus eventBus;
  private final TypeUnloadTracker typeUnloadTracker;
  private final Map<Object, Runnable> unloadHooks;

  public EventBusImpl( TypeUnloadTracker typeUnloadTracker ) {
    verifyNotNull( typeUnloadTracker, "typeUnloadTracker" );

    this.eventBus = new com.google.common.eventbus.EventBus();
    this.unloadHooks = new HashMap<>();
    this.observers = new HashMap<>();
    this.typeUnloadTracker = typeUnloadTracker;
  }

  @Override
  public void dispose() {
    observers.values().forEach( adapters -> adapters.forEach( adapter -> eventBus.unregister( adapter ) ) );
    observers.clear();
  }

  @Override
  public void post( Object eventObject ) {
    verifyNotNull( eventObject, "eventObject" );

    eventBus.post( new EventAdapter( eventObject ) );
  }

  @Override
  public void register( Object eventObserver ) {
    verifyNotNull( eventObserver, "eventObserver" );

    Collection<ObserverAdapter> adapters = adapt( eventObserver );
    if( !adapters.isEmpty() && !observers.containsKey( eventObserver ) ) {
      observers.put( eventObserver, adapters );
      adapters.forEach( adapter -> eventBus.register( adapter ) );
      typeUnloadTracker.registerUnloadHook( eventObserver.getClass(), getUnloadHook( eventObserver ) );
    }
  }

  @Override
  public void unregister( Object eventObserver ) {
    verifyNotNull( eventObserver, "eventObserver" );

    Collection<ObserverAdapter> removed = observers.remove( eventObserver );
    if( removed != null ) {
      removed.forEach( adapter -> eventBus.unregister( adapter ) );
    }
    removeUnloadHook( eventObserver );
  }

  private static Set<ObserverAdapter> adapt( Object eventObserver ) {
    return getAnnotatedMethods( eventObserver, Subscribe.class )
      .stream()
      .map( method -> new ObserverAdapter( method, eventObserver ) )
      .collect( toSet() );
  }

  private Runnable getUnloadHook( Object eventObserver ) {
    Runnable result = () -> unregister( eventObserver );
    unloadHooks.put( eventObserver, result );
    return result;
  }

  private void removeUnloadHook( Object eventObserver ) {
    Runnable hook = unloadHooks.remove( eventObserver );
    if( hook != null ) {
      typeUnloadTracker.unregisterUnloadHook( eventObserver.getClass(), hook );
    }
  }
}