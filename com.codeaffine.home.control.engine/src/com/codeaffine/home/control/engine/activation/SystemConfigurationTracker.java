package com.codeaffine.home.control.engine.activation;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.codeaffine.home.control.SystemConfiguration;

class SystemConfigurationTracker extends ServiceTracker<SystemConfiguration, SystemConfiguration> {

  private final SystemLifeCycle lifeCycle;

  SystemConfigurationTracker( BundleContext context, SystemLifeCycle lifeCycle ) {
    super( context, SystemConfiguration.class, null );
    this.lifeCycle = lifeCycle;
  }

  @Override
  public SystemConfiguration addingService( ServiceReference<SystemConfiguration> reference ) {
    SystemConfiguration configuration = super.addingService( reference );
    lifeCycle.start( configuration );
    return configuration;
  }

  @Override
  public void removedService( ServiceReference<SystemConfiguration> reference, SystemConfiguration configuration ) {
    lifeCycle.stop( configuration );
    super.removedService( reference, configuration );
  }
}