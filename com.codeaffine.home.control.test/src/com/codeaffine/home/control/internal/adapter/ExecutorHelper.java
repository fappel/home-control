package com.codeaffine.home.control.internal.adapter;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mockito.invocation.InvocationOnMock;

import com.codeaffine.home.control.internal.util.SystemExecutor;

public class ExecutorHelper {

  public static SystemExecutor stubInThreadExecutor() {
    SystemExecutor result = mock( SystemExecutor.class );
    doAnswer( invocation -> performExecute( invocation ) )
      .when( result )
      .execute( any( Runnable.class ) );
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void stubWithFutureForFixedRateScheduling( SystemExecutor executor, ScheduledFuture future ) {
    when( executor.scheduleAtFixedRate( any( Runnable.class ) , anyLong(), anyLong(), any( TimeUnit.class ) ) )
      .thenReturn( future );
  }

  public static void blockExecutor( SystemExecutor executor ) {
    doNothing().when( executor ).execute( any( Runnable.class ) );
  }

  private static Object performExecute( InvocationOnMock invocation ) {
    Runnable runnable = ( Runnable )invocation.getArguments()[ 0 ];
    runnable.run();
    return null;
  }
}