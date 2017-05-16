package com.codeaffine.home.control.engine.activation;

import org.osgi.framework.BundleContext;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.engine.component.util.BundleDeactivationTracker;
import com.codeaffine.home.control.engine.wiring.ContextFactory;
import com.codeaffine.home.control.engine.wiring.InjectionStrategy;
import com.codeaffine.util.inject.Context;

class SystemContextFactory implements ContextFactory {

  private final BundleContext context;
  private final Registry registry;

  SystemContextFactory( Registry registry, BundleContext context ) {
    this.registry = registry;
    this.context = context;
  }

  @Override
  public Context create() {
    Context result = new Context( new InjectionStrategy() );
    result.set( Registry.class, registry );
    result.set( BundleDeactivationTracker.class, new BundleDeactivationTracker( context ) );
    return result;
  }
}