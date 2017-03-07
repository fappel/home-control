package com.codeaffine.home.control.internal.activation;

import static com.codeaffine.home.control.internal.activation.Messages.*;

import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.internal.wiring.SystemWiring;
import com.codeaffine.home.control.logger.Logger;

class SystemLifeCycle {

  private final ShutdownDispatcher shutdownDispatcher;
  private final SystemWiring systemWiring;
  private final Logger logger;

  SystemLifeCycle( SystemWiring systemWiring, ShutdownDispatcher shutdownDispatcher ) {
    this( systemWiring, shutdownDispatcher, new LoggerFactoryAdapter().getLogger( SystemLifeCycle.class ) );
  }

  SystemLifeCycle( SystemWiring systemWiring, ShutdownDispatcher shutdownDispatcher, Logger logger ) {
    this.shutdownDispatcher = shutdownDispatcher;
    this.systemWiring = systemWiring;
    this.logger = logger;
  }

  void start( SystemConfiguration configuration ) {
    systemWiring.initialize( configuration );
    logger.info( INFO_SYSTEM_CONFIGURATION_LOADED, configuration.getClass().getName() );
  }

  void stop( SystemConfiguration configuration ) {
    stopConfiguration( () -> systemWiring.reset( configuration ) );
  }

  void dispose() {
    stopConfiguration( () -> systemWiring.dispose() );
  }

  private void stopConfiguration( Runnable configurationStopCommand ) {
    SystemConfiguration configuration = systemWiring.getConfiguration();
    shutdownDispatcher.dispatch();
    configurationStopCommand.run();
    if( configuration != null ) {
      logger.info( INFO_SYSTEM_CONFIGURATION_UNLOADED, configuration.getClass().getName() );
    }
  }
}