package com.codeaffine.home.control.application.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.logger.LoggerFactory;

public class LoggerHelper {

  public static LoggerFactory stubLoggerFactory() {
    LoggerFactory result = mock( LoggerFactory.class );
    Logger logger = mock( Logger.class );
    when( result.getLogger( any() ) ).thenReturn( logger );
    return result;
  }
}