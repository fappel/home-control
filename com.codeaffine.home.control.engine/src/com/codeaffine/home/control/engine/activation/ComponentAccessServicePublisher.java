package com.codeaffine.home.control.engine.activation;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.Context;

class ComponentAccessServicePublisher {

  private final BundleContext bundleContext;

  private ServiceRegistration<ComponentAccessService> serviceRegistration;

  ComponentAccessServicePublisher( BundleContext bundleContext ) {
    this.bundleContext = bundleContext;
  }

  void publish( Context context ) {
    ComponentAccessServiceImpl service = new ComponentAccessServiceImpl( context );
    serviceRegistration = bundleContext.registerService( ComponentAccessService.class, service, null );
  }

  void withdraw() {
    if( serviceRegistration != null ) {
      serviceRegistration.unregister();
      serviceRegistration = null;
    }
  }
}
