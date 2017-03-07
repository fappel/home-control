package com.codeaffine.home.control;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface SystemExecutor {
  void executeAsynchronously( Runnable command );
  void schedule( Runnable command, long delay, TimeUnit unit );
  ScheduledFuture<?> scheduleAtFixedRate( Runnable command, long initialDelay, long period, TimeUnit unit );
}