package com.codeaffine.home.control.engine.activation;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.Context;

public class ComponentAccessServicePublisherTest {

  private ComponentAccessServicePublisher publisher;
  private ServiceRegistration<ComponentAccessService> registration;
  private BundleContext bundleContext;

  @Before
  @SuppressWarnings( "unchecked" )
  public void setUp() {
    registration = mock( ServiceRegistration.class );
    bundleContext = stubBundleContext( registration );
    publisher = new ComponentAccessServicePublisher( bundleContext );
  }

  @Test
  public void publish() {
    Context context = mock( Context.class );

    publisher.publish( context );

    verify( bundleContext )
      .registerService( eq( ComponentAccessService.class ), any( ComponentAccessService.class ), eq( null ) );
  }

  @Test
  public void withdraw() {
    Context context = mock( Context.class );
    publisher.publish( context );

    publisher.withdraw();

    verify( registration ).unregister();
  }

  @Test
  public void withdrawMoreThanOnce() {
    Context context = mock( Context.class );
    publisher.publish( context );

    publisher.withdraw();
    publisher.withdraw();

    verify( registration ).unregister();
  }

  @Test
  public void withdrawIfNotPublished() {
    publisher.withdraw();

    verify( registration, never() ).unregister();
  }

  private static BundleContext stubBundleContext( ServiceRegistration<ComponentAccessService> registration ) {
    BundleContext result = mock( BundleContext.class );
    when( result.registerService( eq( ComponentAccessService.class ), any( ComponentAccessService.class ), eq( null ) ) )
      .thenReturn( registration );
    return result;
  }
}