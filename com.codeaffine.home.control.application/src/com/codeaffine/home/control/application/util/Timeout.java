package com.codeaffine.home.control.application.util;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

public class Timeout {

  public static final long DEFAULT_EXPIRATION_TIME_IN_MINUTES = 1L;

  private final TimeoutPreference preference;

  private LocalDateTime lastActivation;

  public Timeout( TimeoutPreference preference ) {
    this.preference = preference;
    this.lastActivation = now().minus( preference.getExpirationTime() * 2, preference.getTimeUnit() );
  }

  public void set() {
    lastActivation = now();
  }

  public void setIf( boolean condition ) {
    if( condition ) {
      set();
    }
  }

  public boolean isExpired() {
    return now().minus( preference.getExpirationTime(), preference.getTimeUnit() ).isAfter( lastActivation );
  }

  public void executeIfExpired( Runnable command ) {
    verifyNotNull( command, "command" );

    if( isExpired() ) {
      command.run();
    }
  }

  public void executeIfNotExpired( Runnable command ) {
    verifyNotNull( command, "command" );

    if( !isExpired() ) {
      command.run();
    }
  }
}