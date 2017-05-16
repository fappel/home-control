package com.codeaffine.home.control.engine.component.logger;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.codeaffine.home.control.engine.component.logger.LoggerAdapter;


public class LoggerAdapterTest {

  private static final Throwable THROWABLE = new Throwable();
  private static final String MESSAGE = "message";
  private static final String PATTERN_FOR_1_ARG = "pattern %s";
  private static final String PATTERN_FOR_2_ARGS = "pattern %s, %s";
  private static final String PATTERN_FOR_3_ARGS = "pattern %s, %s, %s";
  private static final Object ARG1 = "ARG1";
  private static final Object ARG2 = "ARG2";
  private static final Object ARG3 = "ARG3";
  private static final String NAME = "name";

  private LoggerAdapter logger;
  private Logger delegate;

  @Before
  public void setUp() {
    delegate = mock( Logger.class );
    logger = new LoggerAdapter( delegate );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructLoggerAdapterWithNullAsDelegateArgument() {
    new LoggerAdapter( null );
  }

  @Test
  public void warnWithMessageAndThrowable() {
    logger.warn( MESSAGE, THROWABLE );

    verify( delegate ).warn( MESSAGE, THROWABLE );
  }

  @Test
  public void warnWithPatternAndTwoArguments() {
    stubAsWarnEnabled();

    logger.warn( PATTERN_FOR_2_ARGS, ARG1, ARG2 );
    logger.warn( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, times( 2 ) ).warn( format( PATTERN_FOR_2_ARGS, ARG1, ARG2 ) );
  }

  @Test
  public void warnWithPatternAndTwoArgumentsButWarnLevelDisabled() {
    logger.warn( PATTERN_FOR_1_ARG, ARG1, ARG2 );

    verify( delegate, never() ).warn( anyString() );
  }

  @Test
  public void warnWithPatternAndArgumentArray() {
    stubAsWarnEnabled();

    logger.warn( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );
    logger.warn( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, times( 2 ) ).warn( format( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 ) );
  }

  @Test
  public void warnWithPatternAndArgumentArrayButWarnLevelDisabled() {
    logger.warn( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, never() ).warn( anyString() );
  }

  @Test
  public void warnWithPatternAndArgument() {
    stubAsWarnEnabled();

    logger.warn( PATTERN_FOR_1_ARG, ARG1 );
    logger.warn( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, times( 2 ) ).warn( format( PATTERN_FOR_1_ARG, ARG1 ) );
  }

  @Test
  public void warnWithPatternAndArgumentButWarnLevelDisabled() {
    logger.warn( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, never() ).warn( anyString() );
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
    stubAsTraceEnabled();

    logger.trace( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );
    logger.trace( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, times( 2 ) ).trace( format( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 ) );
  }

  @Test
  public void traceWithPatternAndArgumentArrayButTraceLevelDisabled() {
    logger.trace( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, never() ).trace( anyString() );
  }

  @Test
  public void traceWithPatterAndTwoArguments() {
    stubAsTraceEnabled();

    logger.trace( PATTERN_FOR_2_ARGS, ARG1, ARG2 );
    logger.trace( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, times( 2 ) ).trace( format( PATTERN_FOR_2_ARGS, ARG1, ARG2 ) );
  }

  @Test
  public void traceWithPatterAndTwoArgumentsButTraceLevelDisabled() {
    logger.trace( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, never() ).trace( anyString() );
  }

  @Test
  public void traceWithPatternAndOneArgument() {
    stubAsTraceEnabled();

    logger.trace( PATTERN_FOR_1_ARG, ARG1 );
    logger.trace( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, times( 2 ) ).trace( format( PATTERN_FOR_1_ARG, ARG1 ) );
  }

  @Test
  public void traceWithPatternAndOneArgumentButTraceLevelDisabled() {
    logger.trace( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, never() ).trace( anyString() );
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
    stubAsInfoEnabled();

    logger.info( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );
    logger.info( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, times( 2 ) ).info( format( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 ) );
  }

  @Test
  public void infoWithPatternAndArgumentArrayButInfoLevelDisabled() {
    logger.info( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, never() ).info( anyString() );
  }

  @Test
  public void infoWithPatternAndTwoArguments() {
    stubAsInfoEnabled();

    logger.info( PATTERN_FOR_2_ARGS, ARG1, ARG2 );
    logger.info( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, times( 2 ) ).info( format( PATTERN_FOR_2_ARGS, ARG1, ARG2 ) );
  }

  @Test
  public void infoWithPatternAndTwoArgumentsButInfoLevelDisabled() {
    logger.info( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, never() ).info( anyString() );
  }

  @Test
  public void infoWithPatternAndArgument() {
    stubAsInfoEnabled();

    logger.info( PATTERN_FOR_1_ARG, ARG1 );
    logger.info( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, times( 2 ) ).info( format( PATTERN_FOR_1_ARG, ARG1 ) );
  }

  @Test
  public void infoWithPatternAndArgumentButInfoLevelDisabled() {
    logger.info( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, never() ).info( anyString() );
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
    stubAsErrorEnabled();

    logger.error( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );
    logger.error( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, times( 2 ) ).error( format( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 ) );
  }

  @Test
  public void errorWithPatternAndObjectArrayButErrorLevelDisabled() {
    logger.error( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, never() ).error( anyString() );
  }

  @Test
  public void errorWithPatternAndTwoArguments() {
    stubAsErrorEnabled();

    logger.error( PATTERN_FOR_2_ARGS, ARG1, ARG2 );
    logger.error( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, times( 2 ) ).error( format( PATTERN_FOR_2_ARGS, ARG1, ARG2 ) );
  }

  @Test
  public void errorWithPatternAndTwoArgumentsButErrorLevelDisabled() {
    logger.error( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, never() ).error( anyString() );
  }

  @Test
  public void errorWithPatternAndArgument() {
    stubAsErrorEnabled();

    logger.error( PATTERN_FOR_1_ARG, ARG1 );
    logger.error( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, times( 2 ) ).error( format( PATTERN_FOR_1_ARG, ARG1 ) );
  }

  @Test
  public void errorWithPatternAndArgumentButErrorLevelDisabled() {
    logger.error( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, never() ).error( anyString() );
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
    stubAsDebugEnabled();

    logger.debug( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );
    logger.debug( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, times( 2 ) ).debug( format( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 ) );
  }

  @Test
  public void debugWithPatternAndArgumentArrayButDebugLevelDisabled() {
    logger.debug( PATTERN_FOR_3_ARGS, ARG1, ARG2, ARG3 );

    verify( delegate, never() ).debug( anyString() );
  }

  @Test
  public void debugWithPatternAndTwoArguments() {
    stubAsDebugEnabled();

    logger.debug( PATTERN_FOR_2_ARGS, ARG1, ARG2 );
    logger.debug( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, times( 2 ) ).debug( format( PATTERN_FOR_2_ARGS, ARG1, ARG2 ) );
  }

  @Test
  public void debugWithPatternAndTwoArgumentsButDebugLevelDisabled() {
    logger.debug( PATTERN_FOR_2_ARGS, ARG1, ARG2 );

    verify( delegate, never() ).debug( anyString() );
  }

  @Test
  public void debugWithPatternAndArgument() {
    stubAsDebugEnabled();

    logger.debug( PATTERN_FOR_1_ARG, ARG1 );
    logger.debug( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, times( 2 ) ).debug( format( PATTERN_FOR_1_ARG, ARG1 ) );
  }

  @Test
  public void debugWithPatternAndArgumentButDebugLevelDisabled() {
    logger.debug( PATTERN_FOR_1_ARG, ARG1 );

    verify( delegate, never() ).debug( anyString() );
  }

  @Test
  public void debugWithMessage() {
    logger.debug( MESSAGE );

    verify( delegate ).debug( MESSAGE );
  }

  private void stubAsWarnEnabled() {
    when( delegate.isWarnEnabled() ).thenReturn( true );
  }

  private void stubAsTraceEnabled() {
    when( delegate.isTraceEnabled() ).thenReturn( true );
  }

  private void stubAsInfoEnabled() {
    when( delegate.isInfoEnabled() ).thenReturn( true );
  }

  private void stubAsErrorEnabled() {
    when( delegate.isErrorEnabled() ).thenReturn( true );
  }

  private void stubAsDebugEnabled() {
    when( delegate.isDebugEnabled() ).thenReturn( true );
  }
}