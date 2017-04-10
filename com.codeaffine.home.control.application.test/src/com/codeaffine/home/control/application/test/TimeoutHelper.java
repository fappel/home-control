package com.codeaffine.home.control.application.test;

public class TimeoutHelper {

  public static void sleep( long millis ) {
    try {
      Thread.sleep( millis );
    } catch( InterruptedException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
}