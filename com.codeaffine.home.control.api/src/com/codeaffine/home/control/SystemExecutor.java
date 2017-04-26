package com.codeaffine.home.control;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface SystemExecutor {
  void execute( Runnable command );
  Future<?> submit( Runnable task );
  <T> Future<T> submit( Callable<T> task );
  void schedule( Runnable command, long delay, TimeUnit unit );
  ScheduledFuture<?> scheduleAtFixedRate( Runnable command, long initialDelay, long period, TimeUnit unit );
}