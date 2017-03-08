package com.codeaffine.home.control.engine.wiring;

import static com.codeaffine.home.control.engine.adapter.ExecutorHelper.*;
import static com.codeaffine.home.control.engine.wiring.Messages.*;
import static com.codeaffine.home.control.test.util.entity.MyEntityProvider.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.entity.ZoneEvent;
import com.codeaffine.home.control.entity.ZoneProvider;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.logger.LoggerFactory;
import com.codeaffine.home.control.status.ControlCenter;
import com.codeaffine.home.control.status.SceneSelector;
import com.codeaffine.home.control.status.StatusProviderRegistry;
import com.codeaffine.home.control.test.util.entity.MyEntityDefinition;
import com.codeaffine.home.control.test.util.entity.MyEntityProvider;
import com.codeaffine.home.control.test.util.status.MyHomeControlOperation;
import com.codeaffine.home.control.test.util.status.MyScope;
import com.codeaffine.home.control.test.util.status.MyStatus;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;
import com.codeaffine.home.control.test.util.status.Scene1;
import com.codeaffine.home.control.test.util.status.Scene2;
import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.util.Disposable;

public class SystemWiringTest {

  private static final String ITEM_NAME = "itemName";
  private static final long PERIOD = 0;

  private com.codeaffine.util.inject.Context context;
  private SystemConfiguration configuration;
  private ContextFactory contextFactory;
  private SystemExecutorImpl executor;
  private SystemWiring wiring;
  private NumberItem item;
  @SuppressWarnings( "rawtypes" )
  private ScheduledFuture scheduledFuture;

  static class Bean {

    Entity<EntityDefinition<?>> allocated;

    @Schedule( period = PERIOD )
    private void method(){}

    @Observe( ITEM_NAME )
    void onItemEvent( @SuppressWarnings("unused") ChangeEvent<NumberItem, DecimalType> event ){}

    @Subscribe
    void onBusEvent( ZoneEvent event ) {
      allocated = event.getAdditions().iterator().next();
    }
  }

  static class Configuration implements SystemConfiguration {

    @Override
    public void configureEntities( EntityRegistry entityRegistry ) {
      entityRegistry.register( MyEntityProvider.class );
    }

    @Override
    public void configureFacility( Facility facility ) {
      facility.equip( PARENT ).with( CHILD );
    }

    @Override
    public void configureStatusProvider( StatusProviderRegistry statusProviderRegistry ) {
      statusProviderRegistry.register( MyStatusProvider.class, MyStatusProvider.class );
    }

    @Override
    public void configureHomeControlOperations( ControlCenter controlCenter ) {
      controlCenter.registerOperation( MyHomeControlOperation.class );
    }

    @Override
    public void configureSceneSelection( SceneSelector sceneSelector ) {
      sceneSelector.whenStatusOf( MyScope.GLOBAL, MyStatusProvider.class ).matches( status -> status == MyStatus.ONE )
        .thenSelect( Scene1.class )
      .otherwiseSelect( Scene2.class );
    }

    @Override
    public void configureSystem( Context context ) {
      context.set( Bean.class, context.create( Bean.class ) );
    }
  }

  @Before
  public void setUp() {
    context = new com.codeaffine.util.inject.Context();
    contextFactory = stubContextFactory( context );
    configuration = spy( new Configuration() );
    scheduledFuture = mock( ScheduledFuture.class );
    executor = stubInThreadExecutor();
    stubWithFutureForFixedRateScheduling( executor, scheduledFuture );
    item = mock( NumberItem.class );
    wiring = new SystemWiring( contextFactory, stubRegistry( ITEM_NAME, item ), executor );
  }

  @Test
  public void initialize() {
    wiring.initialize( configuration );

    ArgumentCaptor<Context> contextCaptor = forClass( Context.class );
    verifyInitializationOrder( contextCaptor );
    verifyContextContent( contextCaptor );
    verifyEntitySetup();
    verifyControlCenterSetup();
  }

  @Test
  public void initializeIfExecutorIsBlocked() {
    blockExecutor( executor );

    wiring.initialize( configuration );

    verify( contextFactory, never() ).create();
    verify( executor, never() ).scheduleAtFixedRate( any( Runnable.class ) , anyLong(), anyLong(), any( TimeUnit.class ) );
    verify( configuration, never() ).configureSystem( any() );
    assertThat( wiring.getConfiguration() ).isNotNull();
  }

  @Test
  public void initializeTwice() {
    wiring.initialize( configuration );
    SystemConfiguration otherConfiguration = mock( SystemConfiguration.class );

    Throwable actual = thrownBy( () -> wiring.initialize( otherConfiguration ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_TOO_MANNY_CONFIGURATIONS,
                           configuration.getClass().getName(),
                           otherConfiguration.getClass().getName() ) );
  }

  @Test
  public void reset() {
    Disposable disposable = mock( Disposable.class );
    context.set( Disposable.class, disposable );
    wiring.initialize( configuration );

    wiring.reset( configuration );

    InOrder order = inOrder( scheduledFuture, disposable );
    order.verify( scheduledFuture ).cancel( true );
    order.verify( disposable ).dispose();
    assertThat( wiring.getConfiguration() ).isNull();
  }

  @Test
  public void resetIfExecutorIsBlocked() {
    Disposable disposable = mock( Disposable.class );
    context.set( Disposable.class, disposable );
    wiring.initialize( configuration );
    blockExecutor( executor );

    wiring.reset( configuration );

    verify( scheduledFuture, never() ).cancel( true );
    verify( disposable, never() ).dispose();
    assertThat( wiring.getConfiguration() ).isNotNull();
  }

  @Test
  public void resetIfConfigurationDoesNotMatch() {
    wiring.initialize( configuration );
    SystemConfiguration otherConfiguration = mock( SystemConfiguration.class );

    Throwable actual = thrownBy( () -> wiring.reset( otherConfiguration ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_WRONG_CONFIGURATION_TO_UNLOAD,
                           configuration.getClass().getName(),
                           otherConfiguration.getClass().getName() ) );
  }

  @Test
  public void dispose() {
    Disposable disposable = mock( Disposable.class );
    context.set( Disposable.class, disposable );
    wiring.initialize( configuration );

    wiring.dispose();

    InOrder order = inOrder( scheduledFuture, disposable );
    order.verify( scheduledFuture ).cancel( true );
    order.verify( disposable ).dispose();
    assertThat( wiring.getConfiguration() ).isNull();
  }

  @Test
  public void disposeIfNotInitialized() {
    Throwable actual = thrownBy( () -> wiring.dispose() );

    assertThat( actual ).isNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void busEventWiring() {
    wiring.initialize( configuration );
    Entity<EntityDefinition<?>> expected = mock( Entity.class );

    triggerAllocationEvent( expected );
    Entity<EntityDefinition<?>> actual = context.get( Bean.class ).allocated;

    assertThat( actual ).isSameAs( expected );
    assertThat( context.get( ZoneProvider.class ).getEngagedZones() ).contains( expected );
  }

  private void triggerAllocationEvent( Entity<EntityDefinition<?>> allocatable ) {
    SensorControl factory = context.get( SensorControlFactory.class ).create( mock( Entity.class ) );
    factory.registerZone( allocatable );
    factory.engage();
  }

  @SuppressWarnings( "unchecked" )
  private void verifyInitializationOrder( ArgumentCaptor<Context> contextCaptor ) {
    InOrder order = inOrder( contextFactory, configuration, executor, item );
    order.verify( contextFactory ).create();
    order.verify( configuration ).configureEntities( any( EntityRegistry.class ) );
    order.verify( configuration ).configureFacility( any( Facility.class ) );
    order.verify( configuration ).configureStatusProvider( any( StatusProviderRegistry.class ) );
    order.verify( configuration ).configureHomeControlOperations( any( ControlCenter.class ) );
    order.verify( configuration ).configureSceneSelection( any( SceneSelector.class ) );
    order.verify( configuration ).configureSystem( contextCaptor.capture() );
    order.verify( item ).addChangeListener( any( ChangeListener.class ) );
    order.verify( executor ).scheduleAtFixedRate( any( Runnable.class ) , anyLong(), anyLong(), any( TimeUnit.class ) );
    order.verifyNoMoreInteractions();
  }

  private void verifyContextContent( ArgumentCaptor<Context> contextCaptor ) {
    assertThat( context ).isSameAs( contextCaptor.getValue().get( com.codeaffine.util.inject.Context.class ) );
    assertThat( context.get( EventBus.class ) ).isNotNull();
    assertThat( context.get( ZoneProvider.class ) ).isNotNull();
    assertThat( context.get( LoggerFactory.class ) ).isNotNull();
    assertThat( wiring.getConfiguration() ).isNotNull();
  }

  private void verifyEntitySetup() {
    EntityRelationProvider relationProvider = context.get( EntityRelationProvider.class );
    EntityRegistry entityRegistry = context.get( EntityRegistry.class );
    assertThat( relationProvider.getChildren( PARENT, MyEntityDefinition.class ) ).contains( CHILD );
    assertThat( relationProvider.findByDefinition( PARENT ) ).isNotNull();
    assertThat( entityRegistry.findByDefinition( CHILD ) ).isNotNull();
    assertThat( entityRegistry.findByDefinition( PARENT ) ).isNotNull();
  }

  private void verifyControlCenterSetup() {
    assertThat( context.get( MyStatusProvider.class ) ).isNotNull();
    assertThat( context.get( MyHomeControlOperation.class ) ).isNotNull();
    assertThat( context.get( MyHomeControlOperation.class ).getMyStatusProvider() ).isNotNull();
    assertThat( context.get( Scene1.class ) ).isNotNull();
    assertThat( context.get( Scene2.class ) ).isNotNull();
  }

  private static ContextFactory stubContextFactory( com.codeaffine.util.inject.Context context ) {
    ContextFactory result = mock( ContextFactory.class );
    when( result.create() ).thenReturn( context );
    return result;
  }

  private static Registry stubRegistry( String itemName, NumberItem numberItem ) {
    Registry result = mock( Registry.class );
    when( result.getItem( itemName, NumberItem.class ) ).thenReturn( numberItem );
    return result;
  }
}