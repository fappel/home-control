package com.codeaffine.home.control.status;

public interface Scene {

  String getName();

  default void prepare( @SuppressWarnings( "unused" ) Scene previous ) {
    prepare();
  }

  default void prepare() {}

  default void close() {}
}