package com.codeaffine.home.control.application.scene;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

class Timeout {

  private static final int DEFAULT_TIME_IN_MINUTES = 1;

  private final int timeInMinutes;

  private LocalDateTime lastActivation;

  Timeout() {
    this( DEFAULT_TIME_IN_MINUTES );
  }

  Timeout( int timeInMinutes ) {
    this.timeInMinutes = timeInMinutes;
    this.lastActivation = now().minusMinutes( timeInMinutes * 2 );
  }

  public void set() {
    lastActivation = now();
  }

  public boolean isExpired() {
    return now().minusMinutes( timeInMinutes ).isAfter( lastActivation );
  }
}