package com.codeaffine.home.control.internal.activation;

import static com.codeaffine.home.control.internal.activation.Messages.*;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.internal.wiring.SystemWiring;

class SystemLifeCycle {

  private final ShutdownDispatcher shutdownDispatcher;
  private final SystemWiring systemWiring;
  private final Logger logger;

  SystemLifeCycle( SystemWiring systemWiring, ShutdownDispatcher shutdownDispatcher ) {
    this( systemWiring, shutdownDispatcher, LoggerFactory.getLogger( SystemLifeCycle.class ) );
  }

  SystemLifeCycle( SystemWiring systemWiring, ShutdownDispatcher shutdownDispatcher, Logger logger ) {
    this.shutdownDispatcher = shutdownDispatcher;
    this.systemWiring = systemWiring;
    this.logger = logger;
  }

  void start( SystemConfiguration configuration ) {
    systemWiring.initialize( configuration );
    logger.info( format( INFO_SYSTEM_CONFIGURATION_LOADED, configuration.getClass().getName() ) );
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
      logger.info( format( INFO_SYSTEM_CONFIGURATION_UNLOADED, configuration.getClass().getName() ) );
    }
  }
}