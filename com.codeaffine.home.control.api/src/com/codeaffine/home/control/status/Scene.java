package com.codeaffine.home.control.status;

import static java.util.Optional.empty;

import java.util.Optional;

import com.codeaffine.home.control.status.SceneSelector.Scope;

public interface Scene {

  String getName();

  default Optional<Scope> getScope() {
    return empty();
  }

  default void prepare( @SuppressWarnings( "unused" ) Scene previous ) {
    prepare();
  }

  default void prepare() {}

  default void close() {}

}