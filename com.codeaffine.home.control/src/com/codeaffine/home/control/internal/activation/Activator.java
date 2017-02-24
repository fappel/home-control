package com.codeaffine.home.control.internal.activation;

import static com.codeaffine.home.control.internal.activation.Messages.*;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.codeaffine.home.control.internal.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.internal.wiring.ContextFactory;
import com.codeaffine.home.control.internal.wiring.SystemWiring;
import com.codeaffine.home.control.logger.Logger;

public class Activator implements BundleActivator {

  private final Logger logger = new LoggerFactoryAdapter().getLogger( Activator.class );

  private SystemConfigurationTracker configurationTracker;
  private ShutdownDispatcher shutdownDispatcher;
  private SystemLifeCycle lifeCycle;
  private SystemExecutor executor;

  @Override
  public void start( BundleContext context ) throws Exception {
    shutdownDispatcher = new ShutdownDispatcher();
    executor = createApartmentThreadExecutor();
    SystemWiring systemWiring = createSystemWiring( context, shutdownDispatcher, executor );
    lifeCycle = new SystemLifeCycle( systemWiring, shutdownDispatcher );
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
    BundleContext context, ShutdownDispatcher shutdownDispatcher, SystemExecutor executor )
  {
    ItemRegistryAdapter registry = new ItemRegistryAdapter( context, shutdownDispatcher, executor );
    ContextFactory contextFactory = new SystemContextFactory( registry );
    return new SystemWiring( contextFactory, registry, executor );
  }

  private static SystemConfigurationTracker trackSystemConfiguration( BundleContext ctx, SystemLifeCycle lifeCycle ) {
    SystemConfigurationTracker result = new SystemConfigurationTracker( ctx, lifeCycle );
    result.open();
    return result;
  }

  private static SystemExecutor createApartmentThreadExecutor() {
    return new SystemExecutor( newSingleThreadScheduledExecutor( new SystemThreadFactory() ) );
  }
}