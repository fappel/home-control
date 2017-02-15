package com.codeaffine.home.control.internal.wiring;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.internal.util.SystemExecutor;

class ContextAdapter implements Context {

  private final com.codeaffine.util.inject.Context delegate;
  private final EventBus eventBus;
  private final ItemEventWiring eventWiring;
  private final TimerWiring timer;

  ContextAdapter(
    com.codeaffine.util.inject.Context delegate, Registry registry, SystemExecutor executor, EventBus eventBus )
  {
    this.eventWiring = new ItemEventWiring( registry );
    this.timer = new TimerWiring( executor );
    this.eventBus = eventBus;
    this.delegate = delegate;
    initialize();
  }

  @Override
  public <T> T get( Class<T> key ) {
    return delegate.get( key );
  }

  @Override
  public <T> void set( Class<T> key, T value ) {
    delegate.set( key, value );
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

  private void initialize() {
    set( Context.class, this );
    set( EventBus.class, eventBus );
  }
}