package com.codeaffine.home.control.application.test;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.logger.LoggerFactory;

public class LoggerHelper {

  public static LoggerFactory stubLoggerFactory() {
    LoggerFactory result = mock( LoggerFactory.class );
    Logger logger = mock( Logger.class );
    when( result.getLogger( any() ) ).thenReturn( logger );
    return result;
  }

  public static Object captureSingleInfoArgument( Logger logger, String infoMessage ) {
    ArgumentCaptor<String> captor = forClass( String.class );
    verify( logger ).info( eq( infoMessage ), captor.capture() );
    return captor.getValue();
  }
}