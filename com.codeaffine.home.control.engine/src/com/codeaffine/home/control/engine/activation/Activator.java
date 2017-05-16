package com.codeaffine.home.control.engine.activation;

import static com.codeaffine.home.control.engine.activation.Messages.*;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.codeaffine.home.control.engine.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.engine.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.engine.component.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.engine.wiring.ContextFactory;
import com.codeaffine.home.control.engine.wiring.SystemWiring;
import com.codeaffine.home.control.logger.Logger;

public class Activator implements BundleActivator {

  private final Logger logger = new LoggerFactoryAdapter().getLogger( Activator.class );

  private SystemConfigurationTracker configurationTracker;
  private ShutdownDispatcher shutdownDispatcher;
  private SystemLifeCycle lifeCycle;
  private SystemExecutorImpl executor;

  @Override
  public void start( BundleContext context ) throws Exception {
    shutdownDispatcher = new ShutdownDispatcher();
    executor = createApartmentThreadExecutor();
    SystemWiring systemWiring = createSystemWiring( context, shutdownDispatcher, executor );
    ComponentAccessServicePublisher contextServicePublisher = new ComponentAccessServicePublisher( context );
    lifeCycle = new SystemLifeCycle( systemWiring, contextServicePublisher, shutdownDispatcher );
    logger.info( INFO_SYSTEM_STARTED );
    configurationTracker = trackSystemConfiguration( context, lifeCycle );
  }

  @Override
  public void stop( BundleContext bundleContext ) throws Exception {
    configurationTracker.close();
    executor.shutdown(500L, MILLISECONDS );
    lifeCycle.dispose();
    logger.info( INFO_SYSTEM_STOPPED );
  }

  private static SystemWiring createSystemWiring(
    BundleContext context, ShutdownDispatcher shutdownDispatcher, SystemExecutorImpl executor )
  {
    ItemRegistryAdapter registry = new ItemRegistryAdapter( context, shutdownDispatcher, executor );
    ContextFactory contextFactory = new SystemContextFactory( registry, context );
    return new SystemWiring( contextFactory, registry, executor );
  }

  private static SystemConfigurationTracker trackSystemConfiguration( BundleContext ctx, SystemLifeCycle lifeCycle ) {
    SystemConfigurationTracker result = new SystemConfigurationTracker( ctx, lifeCycle );
    result.open();
    return result;
  }

  private static SystemExecutorImpl createApartmentThreadExecutor() {
    return new SystemExecutorImpl( newSingleThreadScheduledExecutor( new SystemThreadFactory() ) );
  }
}