package com.codeaffine.home.control.engine.activation;

import static com.codeaffine.home.control.engine.activation.Messages.*;

import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.engine.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.engine.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.engine.wiring.SystemWiring;
import com.codeaffine.home.control.logger.Logger;

class SystemLifeCycle {

  private final ComponentAccessServicePublisher componentAccessServicePublisher;
  private final ShutdownDispatcher shutdownDispatcher;
  private final SystemWiring systemWiring;
  private final Logger logger;

  SystemLifeCycle(
    SystemWiring systemWiring,
    ComponentAccessServicePublisher componentAccessServicePublisher,
    ShutdownDispatcher shutdownDispatcher )
  {
    this( systemWiring, componentAccessServicePublisher, shutdownDispatcher, createLogger() );
  }

  SystemLifeCycle(
    SystemWiring systemWiring,
    ComponentAccessServicePublisher componentAccessServicePublisher,
    ShutdownDispatcher shutdownDispatcher,
    Logger logger )
  {
    this.componentAccessServicePublisher = componentAccessServicePublisher;
    this.shutdownDispatcher = shutdownDispatcher;
    this.systemWiring = systemWiring;
    this.logger = logger;
  }

  void start( SystemConfiguration configuration ) {
    systemWiring.initialize( configuration, context -> componentAccessServicePublisher.publish( context ) );
    logger.info( INFO_SYSTEM_CONFIGURATION_LOADED, configuration.getClass().getName() );
  }



  void stop( SystemConfiguration configuration ) {
    stopConfiguration( () -> systemWiring.reset( configuration ) );
  }

  void dispose() {
    stopConfiguration( () -> systemWiring.dispose() );
  }

  private void stopConfiguration( Runnable configurationStopCommand ) {
    componentAccessServicePublisher.withdraw();
    SystemConfiguration configuration = systemWiring.getConfiguration();
    shutdownDispatcher.dispatch();
    configurationStopCommand.run();
    if( configuration != null ) {
      logger.info( INFO_SYSTEM_CONFIGURATION_UNLOADED, configuration.getClass().getName() );
    }
  }

  private static Logger createLogger() {
    return new LoggerFactoryAdapter().getLogger( SystemLifeCycle.class );
  }
}