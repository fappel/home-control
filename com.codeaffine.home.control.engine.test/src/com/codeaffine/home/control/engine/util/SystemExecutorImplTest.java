package com.codeaffine.home.control.engine.util;

import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.logger.Logger;

public class SystemExecutorImplTest {

  private static final String PROBLEM_MESSAGE = "problem-message";
  private static final long INITIAL_DELAY = 10L;
  private static final long TIME_OUT = 13L;
  private static final long PERIOD = 20L;
  private static final long DELAY = 32L;

  private ScheduledExecutorService delegate;
  private SystemExecutorImpl executor;
  private Logger logger;

  @Before
  public void setUp() {
    delegate = mock( ScheduledExecutorService.class );
    logger = mock( Logger.class );
    executor = new SystemExecutorImpl( delegate, logger );
  }

  @Test
  public void shutdown() throws InterruptedException {
    executor.shutdown( TIME_OUT, TimeUnit.DAYS );

    InOrder order = inOrder( delegate );
    order.verify( delegate ).shutdownNow();
    order.verify( delegate ).awaitTermination( TIME_OUT, DAYS );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void execute() {
    Runnable command = mock( Runnable.class );

    executor.execute( command );

    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( delegate ).execute( captor.capture() );
    assertThat( captor.getValue() ).isInstanceOf( SafeRunnable.class );
    captor.getValue().run();
    verify( command ).run();
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void submitRunnableTask() {
    Runnable task = mock( Runnable.class );
    Future expected = mock( Future.class );
    when( delegate.submit( task ) ).thenReturn( expected );

    Future<?> actual = executor.submit( task );

    assertThat( actual ).isSameAs( expected );
    verify( delegate ).submit( task );
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void submitCallableTask() {
    Callable task = mock( Callable.class );
    Future expected = mock( Future.class );
    when( delegate.submit( task ) ).thenReturn( expected );

    Future<?> actual = executor.submit( task );

    assertThat( actual ).isSameAs( expected );
    verify( delegate ).submit( task );
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void scheduleAtFixedRate() {
    Runnable command = mock( Runnable.class );
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    ScheduledFuture expected = mock( ScheduledFuture.class );
    when( delegate.scheduleAtFixedRate( any( Runnable.class ), eq( INITIAL_DELAY ), eq( PERIOD ), eq( DAYS ) ) )
      .thenReturn( expected );

    ScheduledFuture<?> actual = executor.scheduleAtFixedRate( command, INITIAL_DELAY, PERIOD, DAYS );

    assertThat( actual ).isSameAs( expected );
    verify( delegate ).scheduleAtFixedRate( captor.capture(), eq( INITIAL_DELAY ), eq( PERIOD ), eq( DAYS ) );
    assertThat( captor.getValue() ).isInstanceOf( SafeRunnable.class );
    captor.getValue().run();
    verify( command ).run();
  }

  @Test
  public void schedule() {
    Runnable command = mock( Runnable.class );

    executor.schedule( command, DELAY, MINUTES );

    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( delegate ).schedule( captor.capture(), eq( DELAY ), eq( MINUTES ) );
    assertThat( captor.getValue() ).isInstanceOf( SafeRunnable.class );
    captor.getValue().run();
    verify( command ).run();
  }

  @Test
  public void executeWithProblem() {
    Runnable command = mock( Runnable.class );
    RuntimeException problem = new RuntimeException( PROBLEM_MESSAGE );
    doThrow( problem ).when( command ).run();

    executor.execute( command );

    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( delegate ).execute( captor.capture() );
    captor.getValue().run();
    verify( logger ).error( PROBLEM_MESSAGE, problem );
  }
}