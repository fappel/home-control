package com.codeaffine.home.control.engine.util;

import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.engine.util.SafeRunnable;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
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
    public void executeAsynchronously() {
      Runnable command = mock( Runnable.class );

      executor.executeAsynchronously( command );

      ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
      verify( delegate ).execute( captor.capture() );
      assertThat( captor.getValue() ).isInstanceOf( SafeRunnable.class );
      captor.getValue().run();
      verify( command ).run();
    }

  @Test
  public void scheduleAtFixedRate() {
    Runnable command = mock( Runnable.class );

    executor.scheduleAtFixedRate( command, INITIAL_DELAY, PERIOD, DAYS );

    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
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
    public void executeAsynchronouslyWithProblem() {
      Runnable command = mock( Runnable.class );
      RuntimeException problem = new RuntimeException( PROBLEM_MESSAGE );
      doThrow( problem ).when( command ).run();

      executor.executeAsynchronously( command );

      ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
      verify( delegate ).execute( captor.capture() );
      captor.getValue().run();
      verify( logger ).error( PROBLEM_MESSAGE, problem );
    }
}