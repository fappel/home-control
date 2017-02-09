package com.codeaffine.home.control.internal.activation;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.internal.util.SafeRunnable;

class SystemThreadFactory implements ThreadFactory {

  private static final AtomicInteger poolNumber = new AtomicInteger( 1 );

  private final AtomicInteger threadNumber;
  private final ThreadGroup group;
  private final String namePrefix;
  private final Logger logger;

  SystemThreadFactory() {
    this( LoggerFactory.getLogger( Activator.class ) );

  }
  SystemThreadFactory( Logger logger ) {
    this.namePrefix = "home-control-" + poolNumber.getAndIncrement() + "-thread-";
    this.threadNumber = new AtomicInteger( 1 );
    this.group = getThreadGroup();
    this.logger = logger;
  }

  @Override
  public Thread newThread( Runnable runnable ) {
    SafeRunnable safeRunnable = new SafeRunnable( runnable, logger );
    Thread result = new Thread( group, safeRunnable, namePrefix + threadNumber.getAndIncrement(), 0 );
    if( result.isDaemon() ) {
      result.setDaemon( false );
    }
    if( result.getPriority() != Thread.NORM_PRIORITY ) {
      result.setPriority( Thread.NORM_PRIORITY );
    }
    return result;
  }

  private static ThreadGroup getThreadGroup() {
    SecurityManager securityManager = System.getSecurityManager();
    return securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
  }
}