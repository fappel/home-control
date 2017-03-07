package com.codeaffine.home.control.test.util.context;

import com.codeaffine.home.control.Context;

public class TestContext implements Context {

  private final com.codeaffine.util.inject.Context delegate;

  public TestContext() {
    delegate = new com.codeaffine.util.inject.Context();
    delegate.set( Context.class, this );
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
    return delegate.create( type );
  }
}