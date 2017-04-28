package com.codeaffine.home.control.test.util.thread;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mockito.invocation.InvocationOnMock;

import com.codeaffine.home.control.SystemExecutor;

@SuppressWarnings( "unchecked" )
public class ExecutorHelper {

  public static SystemExecutor stubInThreadExecutor() {
    SystemExecutor result = mock( SystemExecutor.class );
    doAnswer( invocation -> performExecute( invocation ) )
      .when( result )
      .execute( any( Runnable.class ) );
    doAnswer( invocation -> performSubmitWithRunnable( invocation ) )
      .when( result )
      .submit( any( Runnable.class ) );
    doAnswer( invocation -> performSubmitWithCallable( invocation ) )
      .when( result )
      .submit( any( Callable.class ) );
    return result;
  }

  @SuppressWarnings({ "rawtypes" })
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

  private static Object performSubmitWithRunnable( InvocationOnMock invocation ) {
    Runnable runnable = ( Runnable )invocation.getArguments()[ 0 ];
    runnable.run();
    Future<?> result = mock( Future.class );
    when( result.isDone() ).thenReturn( true );
    return result;
  }

  private static Object performSubmitWithCallable( InvocationOnMock invocation ) {
    Callable<Object> runnable = ( Callable<Object> )invocation.getArguments()[ 0 ];
    Object returnValue = null;
    ExecutionException executionException = null;
    try {
      returnValue = runnable.call();
    } catch( Exception e ) {
      executionException = new ExecutionException( e );
    }
    Future<Object> result;
    try {
      result = stubCallableFuture( returnValue, executionException );
    } catch( Exception shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
    return result;
  }

  private static Future<Object> stubCallableFuture( Object returnValue, ExecutionException executionException )
    throws InterruptedException, ExecutionException
  {
    Future<Object> result = mock( Future.class );
    when( result.isDone() ).thenReturn( true );
    when( result.get() ).thenReturn( returnValue );
    if( executionException != null ) {
      when( result.get() ).thenThrow( executionException );
    }
    return result;
  }
}