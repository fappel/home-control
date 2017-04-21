package com.codeaffine.home.control.engine.activation;

import static com.codeaffine.home.control.engine.activation.Messages.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.engine.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.engine.wiring.SystemWiring;
import com.codeaffine.home.control.logger.Logger;

public class SystemLifeCycleTest {

  private ComponentAccessServicePublisher componentAccessServicePublisher;
  private ShutdownDispatcher shutdownDispatcher;
  private SystemLifeCycle lifeCycle;
  private SystemWiring wiring;
  private Context context;
  private Logger logger;

  @Before
  public void setUp() {
    context = mock( Context.class );
    wiring = stubWiring( context );
    componentAccessServicePublisher = mock( ComponentAccessServicePublisher.class );
    shutdownDispatcher = mock( ShutdownDispatcher.class );
    logger = mock( Logger.class );
    lifeCycle = new SystemLifeCycle( wiring, componentAccessServicePublisher, shutdownDispatcher, logger );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void start() {
    SystemConfiguration configuration = mock( SystemConfiguration.class );

    lifeCycle.start( configuration );

    InOrder order = inOrder( wiring, logger, componentAccessServicePublisher );
    order.verify( wiring ).initialize( eq( configuration ), any( Consumer.class ) );
    order.verify( componentAccessServicePublisher ).publish( context );
    order.verify( logger ).info( INFO_SYSTEM_CONFIGURATION_LOADED, configuration.getClass().getName() );
  }

  @Test
  public void stop() {
    SystemConfiguration configuration = mock( SystemConfiguration.class );
    stubWiringWithConfiguration( configuration );

    lifeCycle.stop( configuration );

    InOrder order = inOrder( wiring, shutdownDispatcher, logger, componentAccessServicePublisher );
    order.verify( componentAccessServicePublisher ).withdraw();
    order.verify( shutdownDispatcher ).dispatch();
    order.verify( wiring ).reset( configuration );
    order.verify( logger ).info( INFO_SYSTEM_CONFIGURATION_UNLOADED, configuration.getClass().getName() );
  }

  @Test
  public void dispose() {
    SystemConfiguration configuration = mock( SystemConfiguration.class );
    stubWiringWithConfiguration( configuration );

    lifeCycle.dispose();

    InOrder order = inOrder( wiring, shutdownDispatcher, logger, componentAccessServicePublisher );
    order.verify( componentAccessServicePublisher ).withdraw();
    order.verify( shutdownDispatcher ).dispatch();
    order.verify( wiring ).dispose();
    order.verify( logger ).info( INFO_SYSTEM_CONFIGURATION_UNLOADED, configuration.getClass().getName() );
  }

  @Test
  public void disposeWithoutLoadedConfiguration() {
    lifeCycle.dispose();

    InOrder order = inOrder( wiring, shutdownDispatcher, logger, componentAccessServicePublisher );
    order.verify( componentAccessServicePublisher ).withdraw();
    order.verify( shutdownDispatcher ).dispatch();
    order.verify( wiring ).dispose();
    verify( logger, never() ).info( anyString() );
  }

  @SuppressWarnings( "unchecked" )
  private static SystemWiring stubWiring( Context context ) {
    SystemWiring result = mock( SystemWiring.class );
    doAnswer( invocation -> propagateContext( context, invocation ) )
      .when( result ).initialize( any( SystemConfiguration.class ), any( Consumer.class ) );
    return result;
  }

  @SuppressWarnings( "unchecked" )
  private static Context propagateContext( Context context, InvocationOnMock invocation ) {
    Consumer<Context> consumer = ( Consumer<Context> )invocation.getArguments()[ 1 ];
    consumer.accept( context );
    return context;
  }

  private void stubWiringWithConfiguration( SystemConfiguration configuration ) {
    when( wiring.getConfiguration() ).thenReturn( configuration );
  }
}