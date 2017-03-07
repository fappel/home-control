package com.codeaffine.home.control.engine.wiring;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.event.EventBus;

class ContextAdapter implements Context, com.codeaffine.util.Disposable {

  private final com.codeaffine.util.inject.Context delegate;
  private final ItemEventWiring eventWiring;
  private final Set<Disposable> disposables;
  private final EventBus eventBus;
  private final TimerWiring timer;

  ContextAdapter(
    com.codeaffine.util.inject.Context delegate, Registry registry, SystemExecutorImpl executor, EventBus eventBus )
  {
    this.eventWiring = new ItemEventWiring( registry );
    this.timer = new TimerWiring( executor );
    this.disposables = new HashSet<>();
    this.eventBus = eventBus;
    this.delegate = delegate;
    initialize( eventBus, executor );
  }

  @Override
  public void dispose() {
    disposables.forEach( disposable  -> disposable.dispose() );
    disposables.clear();
  }

  @Override
  public <T> T get( Class<T> key ) {
    return delegate.get( key );
  }

  @Override
  public <T> void set( Class<T> key, T value ) {
    delegate.set( key, value );
    if( value instanceof Disposable ) {
      disposables.add( ( Disposable )value );
    }
  }

  @Override
  public <T> T create( Class<T> type ) {
    T result = delegate.create( type );
    eventWiring.wire( result );
    eventBus.register( result );
    timer.schedule( result );
    return result;
  }

  void clearSchedules() {
    timer.reset();
  }

  private void initialize( EventBus eventBus, SystemExecutorImpl executor) {
    set( Context.class, this );
    set( EventBus.class, eventBus );
    set( SystemExecutor.class, executor );
  }
}