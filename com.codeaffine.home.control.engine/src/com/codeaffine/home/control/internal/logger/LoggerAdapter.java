package com.codeaffine.home.control.internal.logger;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Formatter;
import java.util.Locale;

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
  public void warn( String pattern, Object arg1, Object arg2 ) {
    if( logger.isWarnEnabled() ) {
      logger.warn( newFormatter().format( pattern, arg1, arg2 ).toString() );
    }
  }

  @Override
  public void warn( String pattern, Object... arguments ) {
    if( logger.isWarnEnabled() ) {
      logger.warn( newFormatter().format( pattern, arguments ).toString() );
    }
  }

  @Override
  public void warn( String pattern, Object arg ) {
    if( logger.isWarnEnabled() ) {
      logger.warn( newFormatter().format( pattern, arg ).toString() );
    }
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
  public void trace( String pattern, Object... arguments ) {
    if( logger.isTraceEnabled() ) {
      logger.trace( newFormatter().format( pattern, arguments ).toString() );
    }
  }

  @Override
  public void trace( String pattern, Object arg1, Object arg2 ) {
    if( logger.isTraceEnabled() ) {
      logger.trace( newFormatter().format( pattern, arg1, arg2 ).toString() );
    }
  }

  @Override
  public void trace( String pattern, Object arg ) {
    if( logger.isTraceEnabled() ) {
      logger.trace( newFormatter().format( pattern, arg ).toString() );
    }
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
  public void info( String pattern, Object... arguments ) {
    if( logger.isInfoEnabled() ) {
      logger.info( newFormatter().format( pattern, arguments ).toString() );
    }
  }

  @Override
  public void info( String pattern, Object arg1, Object arg2 ) {
    if( logger.isInfoEnabled() ) {
      logger.info( newFormatter().format( pattern, arg1, arg2 ).toString() );
    }
  }

  @Override
  public void info( String pattern, Object arg ) {
    if( logger.isInfoEnabled() ) {
      logger.info( newFormatter().format( pattern, arg ).toString() );
    }
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
  public void error( String pattern, Object... arguments ) {
    if( logger.isErrorEnabled() ) {
      logger.error( newFormatter().format( pattern, arguments ).toString() );
    }
  }

  @Override
  public void error( String pattern, Object arg1, Object arg2 ) {
    if( logger.isErrorEnabled() ) {
      logger.error( newFormatter().format( pattern, arg1, arg2 ).toString() );
    }
  }

  @Override
  public void error( String pattern, Object arg ) {
    if( logger.isErrorEnabled() ) {
      logger.error( newFormatter().format( pattern, arg ).toString() );
    }
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
  public void debug( String pattern, Object... arguments ) {
    if( logger.isDebugEnabled() ) {
      logger.debug( newFormatter().format( pattern, arguments ).toString() );
    }
  }

  @Override
  public void debug( String pattern, Object arg1, Object arg2 ) {
    if( logger.isDebugEnabled() ) {
      logger.debug( newFormatter().format( pattern, arg1, arg2 ).toString() );
    }
  }

  @Override
  public void debug( String pattern, Object arg ) {
    if( logger.isDebugEnabled() ) {
      logger.debug( newFormatter().format( pattern, arg ).toString() );
    }
  }

  @Override
  public void debug( String msg ) {
    logger.debug( msg );
  }

  private static Formatter newFormatter() {
    return new Formatter( ( Locale )null );
  }
}