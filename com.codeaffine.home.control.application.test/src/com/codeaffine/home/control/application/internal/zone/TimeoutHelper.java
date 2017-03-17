package com.codeaffine.home.control.application.internal.zone;

class TimeoutHelper {

  static void waitALittle() {
    try {
      Thread.sleep( 10 );
    } catch( InterruptedException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }
}