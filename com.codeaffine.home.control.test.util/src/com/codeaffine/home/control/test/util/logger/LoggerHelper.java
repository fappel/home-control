package com.codeaffine.home.control.test.util.logger;

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

  public static Object captureSingleDebugArgument( Logger logger, String debugMessage ) {
    ArgumentCaptor<String> captor = forClass( String.class );
    verify( logger ).debug( eq( debugMessage ), captor.capture() );
    return captor.getValue();
  }
}