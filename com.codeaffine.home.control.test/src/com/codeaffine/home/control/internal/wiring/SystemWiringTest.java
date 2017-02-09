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
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.util.Disposable;

public class SystemWiringTest {

  private static final long PERIOD = 0;

  private com.codeaffine.util.inject.Context context;
  private SystemConfiguration configuration;
  private ContextFactory contextFactory;
  private SystemExecutor executor;
  private SystemWiring wiring;
  @SuppressWarnings("rawtypes")
  private ScheduledFuture scheduledFuture;

  static class Bean {
    @Schedule( period = PERIOD )
    private void method(){}
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
    wiring = new SystemWiring( contextFactory, executor );
  }

  @Test
  public void initialize() {
    wiring.initialize( configuration );

    ArgumentCaptor<Context> captor = forClass( Context.class );
    InOrder order = inOrder( contextFactory, configuration, executor );
    order.verify( contextFactory ).create();
    order.verify( configuration ).configureSystem( captor.capture() );
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
}