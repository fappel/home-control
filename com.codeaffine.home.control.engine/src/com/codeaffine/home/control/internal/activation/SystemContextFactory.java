package com.codeaffine.home.control.internal.activation;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.internal.wiring.ContextFactory;
import com.codeaffine.home.control.internal.wiring.InjectionStrategy;
import com.codeaffine.util.inject.Context;

class SystemContextFactory implements ContextFactory {

  private final Registry registry;

  SystemContextFactory( Registry registry ) {
    this.registry = registry;
  }

  @Override
  public Context create() {
    Context result = new Context( new InjectionStrategy() );
    result.set( Registry.class, registry );
    return result;
  }
}