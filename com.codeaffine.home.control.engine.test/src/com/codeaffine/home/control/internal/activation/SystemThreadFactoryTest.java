package com.codeaffine.home.control.internal.activation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.logger.Logger;

public class SystemThreadFactoryTest {

  private static final String PROBLEM_MESSAGE = "bad";

  private SystemThreadFactory factory;
  private Logger logger;

  @Before
  public void setUp() {
    logger = mock( Logger.class );
    factory = new SystemThreadFactory( logger );
  }

  @Test
  public void newThread() {
    Thread actual = factory.newThread( mock( Runnable.class ) );

    assertThat( actual.getThreadGroup() ).isSameAs( Thread.currentThread().getThreadGroup() );
    assertThat( actual.getName() ).containsPattern( "home-control-.-thread-." );
    assertThat( actual.isDaemon() ).isFalse();
    assertThat( actual.getPriority() ).isEqualTo( Thread.NORM_PRIORITY );
  }

  @Test
  public void newThreadExecution() throws InterruptedException {
    Runnable runnable = mock( Runnable.class );

    Thread actual = factory.newThread( runnable );
    actual.start();
    actual.join();

    verify( runnable ).run();
  }

  @Test
  public void newThreadExecutionWithProblem() throws InterruptedException {
    Runnable runnable = mock( Runnable.class );
    RuntimeException problem = new RuntimeException( PROBLEM_MESSAGE );
    doThrow( problem ).when( runnable ).run();

    Thread actual = factory.newThread( runnable );
    actual.start();
    actual.join();

    verify( logger ).error( PROBLEM_MESSAGE, problem );
  }
}
