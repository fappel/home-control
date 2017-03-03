package com.codeaffine.home.control.internal.adapter;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mockito.invocation.InvocationOnMock;

import com.codeaffine.home.control.internal.util.SystemExecutorImpl;

public class ExecutorHelper {

  public static SystemExecutorImpl stubInThreadExecutor() {
    SystemExecutorImpl result = mock( SystemExecutorImpl.class );
    doAnswer( invocation -> performExecute( invocation ) )
      .when( result )
      .executeAsynchronously( any( Runnable.class ) );
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void stubWithFutureForFixedRateScheduling( SystemExecutorImpl executor, ScheduledFuture future ) {
    when( executor.scheduleAtFixedRate( any( Runnable.class ) , anyLong(), anyLong(), any( TimeUnit.class ) ) )
      .thenReturn( future );
  }

  public static void blockExecutor( SystemExecutorImpl executor ) {
    doNothing().when( executor ).executeAsynchronously( any( Runnable.class ) );
  }

  private static Object performExecute( InvocationOnMock invocation ) {
    Runnable runnable = ( Runnable )invocation.getArguments()[ 0 ];
    runnable.run();
    return null;
  }
}