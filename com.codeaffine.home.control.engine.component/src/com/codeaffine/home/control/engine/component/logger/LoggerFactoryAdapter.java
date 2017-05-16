package com.codeaffine.home.control.engine.component.logger;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.logger.LoggerFactory;

public class LoggerFactoryAdapter implements LoggerFactory {

  @Override
  public Logger getLogger( Class<?> clazz ) {
    verifyNotNull( clazz, "clazz" );

    return new LoggerAdapter( org.slf4j.LoggerFactory.getLogger( clazz ));
  }
}
