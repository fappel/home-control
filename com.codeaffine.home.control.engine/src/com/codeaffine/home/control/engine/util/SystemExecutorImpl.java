package com.codeaffine.home.control.engine.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.engine.component.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.logger.Logger;

public class SystemExecutorImpl implements SystemExecutor {

  private final ScheduledExecutorService delegate;
  private final Logger logger;

  public SystemExecutorImpl( ScheduledExecutorService delegate ) {
    this( delegate, new LoggerFactoryAdapter().getLogger( ScheduledExecutorService.class ) );
  }

  SystemExecutorImpl( ScheduledExecutorService delegate, Logger logger ) {
    this.delegate = delegate;
    this.logger = logger;
  }

  public void shutdown( long timeout, TimeUnit unit ) throws InterruptedException {
    delegate.shutdownNow();
    delegate.awaitTermination( timeout, unit );
  }

  @Override
  public void execute( Runnable command ) {
    delegate.execute( wrapCommand( command ) );
  }

  @Override
  public Future<?> submit( Runnable task ) {
    return delegate.submit( task );
  }

  @Override
  public <T> Future<T> submit( Callable<T> task ) {
    return delegate.submit( task );
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate( Runnable command, long initialDelay, long period, TimeUnit unit ) {
    return delegate.scheduleAtFixedRate( wrapCommand( command ), initialDelay, period, unit );
  }

  @Override
  public void schedule( Runnable command, long delay, TimeUnit unit ) {
    delegate.schedule( wrapCommand( command ), delay, unit );
  }

  private SafeRunnable wrapCommand( Runnable command ) {
    return new SafeRunnable( command, logger );
  }
}