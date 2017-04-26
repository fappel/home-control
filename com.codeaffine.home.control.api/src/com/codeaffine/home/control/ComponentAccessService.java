package com.codeaffine.home.control;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ComponentAccessService {

  interface ComponentSupplier {
    <T> T get( Class<T> key );
  }

  void execute( Consumer<ComponentSupplier> command );
  <T> T submit( Function<ComponentSupplier, T> task );
}