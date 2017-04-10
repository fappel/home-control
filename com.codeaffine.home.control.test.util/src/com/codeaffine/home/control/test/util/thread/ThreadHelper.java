package com.codeaffine.home.control.test.util.thread;

public class ThreadHelper {

  public static void sleep( long millis ) {
    try {
      Thread.sleep( millis );
    } catch( InterruptedException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
}