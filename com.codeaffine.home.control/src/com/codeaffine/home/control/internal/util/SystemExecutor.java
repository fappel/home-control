package com.codeaffine.home.control.internal.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.codeaffine.home.control.internal.logger.LoggerFactoryAdapter;
import com.codeaffine.home.control.logger.Logger;

public class SystemExecutor {

  private final ScheduledExecutorService delegate;
  private final Logger logger;

  public SystemExecutor( ScheduledExecutorService delegate ) {
    this( delegate, new LoggerFactoryAdapter().getLogger( ScheduledExecutorService.class ) );
  }

  SystemExecutor( ScheduledExecutorService delegate, Logger logger ) {
    this.delegate = delegate;
    this.logger = logger;
  }

  public void shutdown( long timeout, TimeUnit unit ) throws InterruptedException {
    delegate.shutdownNow();
    delegate.awaitTermination( timeout, unit );
  }

  public void execute( Runnable command ) {
    delegate.execute( new SafeRunnable( command, logger ) );
  }

  public ScheduledFuture<?> scheduleAtFixedRate( Runnable command, long initialDelay, long period, TimeUnit unit ) {
    return delegate.scheduleAtFixedRate( new SafeRunnable( command, logger ), initialDelay, period, unit );
  }
}