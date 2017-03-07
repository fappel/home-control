package com.codeaffine.home.control.engine.activation;

import static com.codeaffine.home.control.engine.activation.Messages.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.engine.activation.SystemLifeCycle;
import com.codeaffine.home.control.engine.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.engine.wiring.SystemWiring;
import com.codeaffine.home.control.logger.Logger;

public class SystemLifeCycleTest {

  private ShutdownDispatcher shutdownDispatcher;
  private SystemLifeCycle lifeCycle;
  private SystemWiring wiring;
  private Logger logger;

  @Before
  public void setUp() {
    wiring = mock( SystemWiring.class );
    shutdownDispatcher = mock( ShutdownDispatcher.class );
    logger = mock( Logger.class );
    lifeCycle = new SystemLifeCycle( wiring, shutdownDispatcher, logger );
  }

  @Test
  public void start() {
    SystemConfiguration configuration = mock( SystemConfiguration.class );

    lifeCycle.start( configuration );

    InOrder order = inOrder( wiring, logger );
    order.verify( wiring ).initialize( configuration );
    order.verify( logger ).info( INFO_SYSTEM_CONFIGURATION_LOADED, configuration.getClass().getName() );
  }

  @Test
  public void stop() {
    SystemConfiguration configuration = mock( SystemConfiguration.class );
    stubWiringWithConfiguration( configuration );

    lifeCycle.stop( configuration );

    InOrder order = inOrder( wiring, shutdownDispatcher, logger );
    order.verify( shutdownDispatcher ).dispatch();
    order.verify( wiring ).reset( configuration );
    order.verify( logger ).info( INFO_SYSTEM_CONFIGURATION_UNLOADED, configuration.getClass().getName() );
  }

  @Test
  public void dispose() {
    SystemConfiguration configuration = mock( SystemConfiguration.class );
    stubWiringWithConfiguration( configuration );

    lifeCycle.dispose();

    InOrder order = inOrder( wiring, shutdownDispatcher, logger );
    order.verify( shutdownDispatcher ).dispatch();
    order.verify( wiring ).dispose();
    order.verify( logger ).info( INFO_SYSTEM_CONFIGURATION_UNLOADED, configuration.getClass().getName() );
  }

  @Test
  public void disposeWithoutLoadedConfiguration() {
    lifeCycle.dispose();

    InOrder order = inOrder( wiring, shutdownDispatcher, logger );
    order.verify( shutdownDispatcher ).dispatch();
    order.verify( wiring ).dispose();
    verify( logger, never() ).info( anyString() );
  }

  private void stubWiringWithConfiguration( SystemConfiguration configuration ) {
    when( wiring.getConfiguration() ).thenReturn( configuration );
  }
}