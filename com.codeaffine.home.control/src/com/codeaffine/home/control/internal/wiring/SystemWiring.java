package com.codeaffine.home.control.internal.wiring;

import static com.codeaffine.home.control.internal.wiring.Messages.*;
import static java.lang.String.format;

import java.util.function.Predicate;

import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.util.inject.Context;

public class SystemWiring {

  private final ContextFactory contextFactory;
  private final SystemExecutor executor;

  private volatile SystemConfiguration configuration;
  private volatile ContextAdapter contextAdapter;
  private volatile Context context;

  public SystemWiring( ContextFactory contextFactory, SystemExecutor executor ) {
    this.contextFactory = contextFactory;
    this.executor = executor;
  }

  public void initialize( SystemConfiguration configuration ) {
    verifyNoConfigurationIsLoaded( configuration );

    this.configuration = configuration;
    executor.execute( () -> doInitialize() );
  }

  public void reset( SystemConfiguration configuration ) {
    verifyToUnloadMatchesLoadedConfiguration( configuration );

    executor.execute( () -> doReset() );
  }

  public void dispose() {
    if( contextAdapter != null ) {
      contextAdapter.clearSchedules();
    }
    if( context != null ) {
      context.dispose();
    }
    contextAdapter = null;
    configuration = null;
    context = null;
  }

  public SystemConfiguration getConfiguration() {
    return configuration;
  }

  private void doReset() {
    dispose();
  }

  private void doInitialize() {
    context = contextFactory.create();
    contextAdapter = new ContextAdapter( context, executor );
    configuration.configureSystem( contextAdapter );
  }

  private void verifyNoConfigurationIsLoaded( SystemConfiguration toLoad ) {
    verifyConfiguration( requested -> configuration != null, toLoad, ERROR_TOO_MANNY_CONFIGURATIONS );
  }

  private void verifyToUnloadMatchesLoadedConfiguration( SystemConfiguration toUnload ) {
    verifyConfiguration( requested -> configuration != requested, toUnload, ERROR_WRONG_CONFIGURATION_TO_UNLOAD );
  }

  private void verifyConfiguration(
    Predicate<SystemConfiguration> predicate, SystemConfiguration requested, String pattern )
  {
    if( predicate.test( requested ) ) {
      String message = format( pattern, configuration.getClass().getName(), requested.getClass().getName() );
      throw new IllegalStateException( message );
    }
  }
}