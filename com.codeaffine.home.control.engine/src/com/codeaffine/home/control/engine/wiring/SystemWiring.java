package com.codeaffine.home.control.engine.wiring;

import static com.codeaffine.home.control.engine.wiring.Messages.*;
import static java.lang.String.format;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.engine.entity.EntityRegistryImpl;
import com.codeaffine.home.control.engine.entity.EntityRelationProviderImpl;
import com.codeaffine.home.control.engine.entity.SensorControlFactoryImpl;
import com.codeaffine.home.control.engine.event.EventBusImpl;
import com.codeaffine.home.control.engine.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.engine.preference.PreferenceModelImpl;
import com.codeaffine.home.control.engine.preference.PreferencePersistence;
import com.codeaffine.home.control.engine.status.ControlCenterImpl;
import com.codeaffine.home.control.engine.status.StatusProviderRegistryImpl;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.logger.LoggerFactory;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.util.inject.Context;

public class SystemWiring {

  private final ContextFactory contextFactory;
  private final SystemExecutor executor;
  private final Registry registry;

  private volatile SystemConfiguration configuration;
  private volatile ContextAdapter contextAdapter;
  private volatile Context context;

  public SystemWiring( ContextFactory contextFactory, Registry registry, SystemExecutor executor ) {
    this.contextFactory = contextFactory;
    this.registry = registry;
    this.executor = executor;
  }

  public void initialize(
    SystemConfiguration configuration, Consumer<com.codeaffine.home.control.Context> contextConsumer )
  {
    verifyNoConfigurationIsLoaded( configuration );

    this.configuration = configuration;
    executor.execute( () -> doInitialize( contextConsumer ) );
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

  private void doInitialize( Consumer<com.codeaffine.home.control.Context> contextConsumer ) {
    context = contextFactory.create();
    contextAdapter = new ContextAdapter( context, registry, executor, new EventBusImpl() );
    contextAdapter.set( PreferenceModel.class, context.create( PreferenceModelImpl.class ) );
    contextAdapter.set( PreferencePersistence.class, context.create( PreferencePersistence.class ) );
    contextAdapter.set( LoggerFactory.class, contextAdapter.create( LoggerFactoryAdapter.class ) );
    contextAdapter.set( SensorControlFactory.class, contextAdapter.create( SensorControlFactoryImpl.class ) );
    EntityRegistry entityRegistry = contextAdapter.create( EntityRegistryImpl.class );
    contextAdapter.set( EntityRegistry.class, entityRegistry );
    EntityRelationProviderImpl entityRelationProvider = contextAdapter.create( EntityRelationProviderImpl.class );
    contextAdapter.set( EntityRelationProvider.class, entityRelationProvider );
    configuration.configureEntities( entityRegistry );
    entityRelationProvider.establishRelations( configuration );
    configuration.configureStatusSupplier( new StatusProviderRegistryImpl( contextAdapter ) );
    ControlCenterImpl controlCenter = contextAdapter.create( ControlCenterImpl.class );
    contextAdapter.set( FollowUpTimer.class, controlCenter );
    configuration.configureHomeControlOperations( controlCenter );
    configuration.configureSceneSelection( controlCenter );
    configuration.configureSystem( contextAdapter );
    contextConsumer.accept( contextAdapter );
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