package com.codeaffine.home.control.internal.wiring;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.internal.util.SystemExecutor;

class ContextAdapter implements Context {

  private final com.codeaffine.util.inject.Context delegate;
  private final Timer timer;

  ContextAdapter( com.codeaffine.util.inject.Context delegate, SystemExecutor executor ) {
    this.timer = new Timer( executor );
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
    timer.schedule( result );
    return result;
  }

  void clearSchedules() {
    timer.reset();
  }
}