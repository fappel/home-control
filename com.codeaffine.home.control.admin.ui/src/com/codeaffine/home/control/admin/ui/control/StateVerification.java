package com.codeaffine.home.control.admin.ui.control;

import static java.lang.String.format;

class StateVerification {

  static void verifyState( boolean condition, String pattern, Object ... arguments ) {
    if( !condition ) {
      throw new IllegalStateException( format( pattern, arguments ) );
    }
  }
}