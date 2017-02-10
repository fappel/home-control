package com.codeaffine.home.control.internal.wiring;

import static com.codeaffine.home.control.internal.adapter.ExecutorHelper.*;
import static com.codeaffine.home.control.internal.wiring.Messages.*;
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
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.util.Disposable;

public class SystemWiringTest {

  private static final String ITEM_NAME = "itemName";
  private static final long PERIOD = 0;

  private com.codeaffine.util.inject.Context context;
  private SystemConfiguration configuration;
  private ContextFactory contextFactory;
  private SystemExecutor executor;
  private SystemWiring wiring;
  private NumberItem item;
  @SuppressWarnings( "rawtypes" )
  private ScheduledFuture scheduledFuture;

  static class Bean {

    @Schedule( period = PERIOD )
    private void method(){}

    @Observe( ITEM_NAME )
    private void onEvent( @SuppressWarnings("unused") ChangeEvent<NumberItem, DecimalType> event ){}
  }

  static class Configuration implements SystemConfiguration {

    @Override
    public void configureSystem( Context context ) {
      context.create( Bean.class );
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
  @SuppressWarnings( "unchecked" )
  public void initialize() {
    wiring.initialize( configuration );

    ArgumentCaptor<Context> captor = forClass( Context.class );
    InOrder order = inOrder( contextFactory, configuration, executor, item );
    order.verify( contextFactory ).create();
    order.verify( configuration ).configureSystem( captor.capture() );
    order.verify( item ).addChangeListener( any( ChangeListener.class ) );
    order.verify( executor ).scheduleAtFixedRate( any( Runnable.class ) , anyLong(), anyLong(), any( TimeUnit.class ) );
    order.verifyNoMoreInteractions();
    assertThat( context ).isSameAs( captor.getValue().get( com.codeaffine.util.inject.Context.class ) );
    assertThat( wiring.getConfiguration() ).isNotNull();
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