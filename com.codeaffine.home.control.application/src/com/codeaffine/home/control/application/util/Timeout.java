package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.util.Messages.TIMEOUT_EXIRATION_TIME_VALUE_TOO_SMALL;
import static com.codeaffine.util.ArgumentVerification.*;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Timeout {

  public static final long DEFAULT_EXPIRATION_TIME_IN_MINUTES = 1L;

  private final long expirationTime;
  private final ChronoUnit timeUnit;

  private LocalDateTime lastActivation;

  public Timeout() {
    this( DEFAULT_EXPIRATION_TIME_IN_MINUTES, MINUTES );
  }

  public Timeout( long expirationTime, ChronoUnit timeUnit ) {
    verifyCondition( expirationTime > 0, TIMEOUT_EXIRATION_TIME_VALUE_TOO_SMALL, Long.valueOf( expirationTime ) );
    verifyNotNull( timeUnit, "timeUnit" );

    this.expirationTime = expirationTime;
    this.timeUnit = timeUnit;
    this.lastActivation = now().minus( expirationTime * 2, timeUnit );
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
    return now().minus( expirationTime, timeUnit ).isAfter( lastActivation );
  }
}