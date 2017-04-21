package com.codeaffine.home.control;

import java.util.function.Consumer;

public interface ComponentAccessService {

  interface ComponentSupplier {
    <T> T get( Class<T> key );
  }

  void execute( Consumer<ComponentSupplier> command );
}