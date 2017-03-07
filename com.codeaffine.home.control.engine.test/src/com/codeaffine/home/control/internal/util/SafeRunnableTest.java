package com.codeaffine.home.control.internal.util;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.logger.Logger;

public class SafeRunnableTest {

  private static final String PROBLEM_MESSAGE = "problem-message";

  private SafeRunnable safeRunnable;
  private Runnable delegate;
  private Logger logger;

  @Before
  public void setUp() {
    delegate = mock( Runnable.class );
    logger = mock( Logger.class );
    safeRunnable = new SafeRunnable( delegate, logger );
  }

  @Test
  public void run() {
    safeRunnable.run();

    verify( delegate ).run();
    verifyNoMoreInteractions( logger );
  }

  @Test
  public void runWithProblem() {
    RuntimeException toBeThrown = new RuntimeException( PROBLEM_MESSAGE );
    doThrow( toBeThrown ).when( delegate ).run();

    safeRunnable.run();

    verify( logger ).error( PROBLEM_MESSAGE, toBeThrown );
  }
}