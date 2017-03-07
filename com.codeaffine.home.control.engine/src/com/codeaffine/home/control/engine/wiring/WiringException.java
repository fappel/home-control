package com.codeaffine.home.control.engine.wiring;

public class WiringException extends RuntimeException {

  private static final long serialVersionUID = -2903622831914891758L;

  public WiringException( String message ) {
    super( message );
  }

  public WiringException( Throwable cause ) {
    super( cause );
  }

  public WiringException( String message, Throwable cause ) {
    super( message, cause );
  }
}
