package com.codeaffine.home.control.internal.logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;


public class LoggerAdapterTest {

  private static final Throwable THROWABLE = new Throwable();
  private static final String MESSAGE = "message";
  private static final String PATTERN = "pattern";
  private static final Object ARG1 = new Object();
  private static final Object ARG2 = new Object();
  private static final Object ARG3 = new Object();
  private static final String NAME = "name";

  private LoggerAdapter logger;
  private Logger delegate;

  @Before
  public void setUp() {
    delegate = mock( Logger.class );
    logger = new LoggerAdapter( delegate );
  }

  @Test( expected = IllegalArgumentException.class )
  public void LoggerAdapterWitnNullArgument() {
    new LoggerAdapter( null );
  }

  @Test
  public void warnWithMessageAndThrowable() {
    logger.warn( MESSAGE, THROWABLE );

    verify( delegate ).warn( MESSAGE, THROWABLE );
  }

  @Test
  public void warnWithPatternAndTwoArguments() {
    logger.warn( PATTERN, ARG1, ARG2 );

    verify( delegate ).warn( PATTERN, ARG1, ARG2 );
  }

  @Test
  public void warnWithPatternAndArgumentArray() {
    logger.warn( PATTERN, ARG1, ARG2, ARG3 );

    verify( delegate ).warn( PATTERN, ARG1, ARG2, ARG3 );
  }

  @Test
  public void warnWithPatternAndArgument() {
    logger.warn( PATTERN, ARG1 );

    verify( delegate ).warn( PATTERN, ARG1 );
  }

  @Test
  public void warnWithMessage() {
    logger.warn( MESSAGE );

    verify( delegate ).warn( MESSAGE );
  }

  @Test
  public void traceWithMessageAndThrowable() {
    logger.trace( MESSAGE, THROWABLE );

    verify( delegate ).trace( MESSAGE, THROWABLE );
  }

  @Test
  public void traceWithPatternAndArgumentArray() {
    logger.trace( PATTERN, ARG1, ARG2, ARG3 );

    verify( delegate ).trace( PATTERN, ARG1, ARG2, ARG3 );
  }

  @Test
  public void traceWithPatterAndTwoArguments() {
    logger.trace( PATTERN, ARG1, ARG2 );

    verify( delegate ).trace( PATTERN, ARG1, ARG2 );
  }

  @Test
  public void traceWithPatternAndOneArgument() {
    logger.trace( PATTERN, ARG1 );

    verify( delegate ).trace( PATTERN, ARG1 );
  }

  @Test
  public void traceWithMessage() {
    logger.trace( MESSAGE );

    verify( delegate ).trace( MESSAGE );
  }

  @Test
  public void isWarnEnabled() {
    when( delegate.isWarnEnabled() ).thenReturn( true );

    boolean actual = logger.isWarnEnabled();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isTraceEnabled() {
    when( delegate.isTraceEnabled() ).thenReturn( true );

    boolean actual = logger.isTraceEnabled();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isInfoEnabled() {
    when( delegate.isInfoEnabled() ).thenReturn( true );

    boolean actual = logger.isInfoEnabled();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isErrorEnabled() {
    when( delegate.isErrorEnabled() ).thenReturn( true );

    boolean actual = logger.isErrorEnabled();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isDebugEnabled() {
    when( delegate.isDebugEnabled() ).thenReturn( true );

    boolean actual = logger.isDebugEnabled();

    assertThat( actual ).isTrue();
  }

  @Test
  public void infoWithMessageAndThrowable() {
    logger.info( MESSAGE, THROWABLE );

    verify( delegate ).info( MESSAGE, THROWABLE );
  }

  @Test
  public void infoWithPatternAndArgumentArray() {
    logger.info( PATTERN, ARG1, ARG2, ARG3 );

    verify( delegate ).info( PATTERN, ARG1, ARG2, ARG3 );
  }

  @Test
  public void infoWithPatternAndTwoArguments() {
    logger.info( PATTERN, ARG1, ARG2 );

    verify( delegate ).info( PATTERN, ARG1, ARG2 );
  }

  @Test
  public void infoWithPatternAndArgument() {
    logger.info( PATTERN, ARG1 );

    verify( delegate ).info( PATTERN, ARG1 );
  }

  @Test
  public void infoWithMessage() {
    logger.info( MESSAGE );

    verify( delegate ).info( MESSAGE );
  }

  @Test
  public void getName() {
    when( delegate.getName() ).thenReturn( NAME );

    String actual = logger.getName();

    assertThat( actual ).isEqualTo( NAME );
  }

  @Test
  public void errorWithMessageAndThrowable() {
    logger.error( MESSAGE, THROWABLE );

    verify( delegate ).error( MESSAGE, THROWABLE );
  }

  @Test
  public void errorWithPatternAndObjectArray() {
    logger.error( PATTERN, ARG1, ARG2, ARG3 );

    verify( delegate ).error( PATTERN, ARG1, ARG2, ARG3 );
  }

  @Test
  public void errorWithPatternAndTwoArguments() {
    logger.error( PATTERN, ARG1, ARG2 );

    verify( delegate ).error( PATTERN, ARG1, ARG2 );
  }

  @Test
  public void errorWithPatternAndArgument() {
    logger.error( PATTERN, ARG1 );

    verify( delegate ).error( PATTERN, ARG1 );
  }

  @Test
  public void errorWithMessage() {
    logger.error( MESSAGE );

    verify( delegate ).error( MESSAGE );
  }

  @Test
  public void debugWithMessageAndThrowable() {
    logger.debug( MESSAGE, THROWABLE );

    verify( delegate ).debug( MESSAGE, THROWABLE );
  }

  @Test
  public void debugWithPatternAndArgumentArray() {
    logger.debug( PATTERN, ARG1, ARG2, ARG3 );

    verify( delegate ).debug( PATTERN, ARG1, ARG2, ARG3 );
  }

  @Test
  public void debugWithPatternAndTwoArguments() {
    logger.debug( PATTERN, ARG1, ARG2 );

    verify( delegate ).debug( PATTERN, ARG1, ARG2 );
  }

  @Test
  public void debugWithPatternAndArgument() {
    logger.debug( PATTERN, ARG1 );

    verify( delegate ).debug( PATTERN, ARG1 );
  }

  @Test
  public void debugWithMessage() {
    logger.debug( MESSAGE );

    verify( delegate ).debug( MESSAGE );
  }
}
