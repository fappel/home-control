package com.codeaffine.home.control.internal.logger;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.logger.Logger;

class LoggerAdapter implements Logger {

  private final org.slf4j.Logger logger;

  public LoggerAdapter( org.slf4j.Logger logger ) {
    verifyNotNull( logger, "logger" );

    this.logger = logger;
  }

  @Override
  public void warn( String msg, Throwable t ) {
    logger.warn( msg, t );
  }

  @Override
  public void warn( String format, Object arg1, Object arg2 ) {
    logger.warn( format, arg1, arg2 );
  }

  @Override
  public void warn( String format, Object... arguments ) {
    logger.warn( format, arguments );
  }

  @Override
  public void warn( String format, Object arg ) {
    logger.warn( format, arg );
  }

  @Override
  public void warn( String msg ) {
    logger.warn( msg );
  }

  @Override
  public void trace( String msg, Throwable t ) {
    logger.trace( msg, t );
  }

  @Override
  public void trace( String format, Object... arguments ) {
    logger.trace( format, arguments );
  }

  @Override
  public void trace( String format, Object arg1, Object arg2 ) {
    logger.trace( format, arg1, arg2 );
  }

  @Override
  public void trace( String format, Object arg ) {
    logger.trace( format, arg );
  }

  @Override
  public void trace( String msg ) {
    logger.trace( msg );
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void info( String msg, Throwable t ) {
    logger.info( msg, t );
  }

  @Override
  public void info( String format, Object... arguments ) {
    logger.info( format, arguments );
  }

  @Override
  public void info( String format, Object arg1, Object arg2 ) {
    logger.info( format, arg1, arg2 );
  }

  @Override
  public void info( String format, Object arg ) {
    logger.info( format, arg );
  }

  @Override
  public void info( String msg ) {
    logger.info( msg );
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  @Override
  public void error( String msg, Throwable t ) {
    logger.error( msg, t );
  }

  @Override
  public void error( String format, Object... arguments ) {
    logger.error( format, arguments );
  }

  @Override
  public void error( String format, Object arg1, Object arg2 ) {
    logger.error( format, arg1, arg2 );
  }

  @Override
  public void error( String format, Object arg ) {
    logger.error( format, arg );
  }

  @Override
  public void error( String msg ) {
    logger.error( msg );
  }

  @Override
  public void debug( String msg, Throwable t ) {
    logger.debug( msg, t );
  }

  @Override
  public void debug( String format, Object... arguments ) {
    logger.debug( format, arguments );
  }

  @Override
  public void debug( String format, Object arg1, Object arg2 ) {
    logger.debug( format, arg1, arg2 );
  }

  @Override
  public void debug( String format, Object arg ) {
    logger.debug( format, arg );
  }

  @Override
  public void debug( String msg ) {
    logger.debug( msg );
  }
}