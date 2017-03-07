package com.codeaffine.home.control;

public interface Context {

  public interface Disposable {
    void dispose();
  }

  <T> T get( Class<T> key );
  <T> void set( Class<T> key, T value );
  <T> T create( Class<T> type );
}
