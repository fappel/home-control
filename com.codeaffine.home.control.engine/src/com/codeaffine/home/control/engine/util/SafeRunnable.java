package com.codeaffine.home.control.engine.util;

import com.codeaffine.home.control.logger.Logger;

public class SafeRunnable implements Runnable {

  private final Runnable delegate;
  private final Logger logger;

  public SafeRunnable( Runnable delegate, Logger logger ) {
    this.delegate = delegate;
    this.logger = logger;
  }

  @Override
  public void run() {
    try {
      delegate.run();
    } catch( Exception e ) {
      logger.error( e.getMessage(), e );
    }
  }
}