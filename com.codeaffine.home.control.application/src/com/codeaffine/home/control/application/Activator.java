package com.codeaffine.home.control.application;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.codeaffine.home.control.SystemConfiguration;

public class Activator implements BundleActivator {

  private ServiceRegistration<SystemConfiguration> configurationRegistration;

  @Override
  public void start( BundleContext context ) throws Exception {
    Configuration configuration = new Configuration();
    Class<SystemConfiguration> type = SystemConfiguration.class;
    configurationRegistration = context.registerService( type, configuration, null );
  }

  @Override
  public void stop( BundleContext bundleContext ) throws Exception {
    configurationRegistration.unregister();
  }
}