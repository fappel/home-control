package com.codeaffine.home.control.internal.wiring;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.internal.util.SystemExecutor;

class ContextAdapter implements Context {

  private final com.codeaffine.util.inject.Context delegate;
  private final EventWiring eventWiring;
  private final TimerWiring timer;

  ContextAdapter( com.codeaffine.util.inject.Context delegate, Registry registry, SystemExecutor executor ) {
    this.eventWiring = new EventWiring( registry );
    this.timer = new TimerWiring( executor );
    this.delegate = delegate;
    set( Context.class, this );
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
    timer.schedule( result );
    return result;
  }

  void clearSchedules() {
    timer.reset();
  }
}