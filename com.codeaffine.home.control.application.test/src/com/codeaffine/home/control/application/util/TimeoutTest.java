package com.codeaffine.home.control.application.util;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

public class TimeoutTest {

  private static final ChronoUnit TIME_UNIT = ChronoUnit.MILLIS;
  private static final long EXPIRATION_TIME = 10L;

  private Timeout timeout;

  @Before
  public void setUp() {
    timeout = new Timeout( EXPIRATION_TIME, TIME_UNIT );
  }

  @Test
  public void initial() {
    assertThat( timeout.isExpired() ).isTrue();
  }

  @Test
  public void set() {
    timeout.set();

    assertThat( timeout.isExpired() ).isFalse();
  }

  @Test
  public void setIfWithTrueAsConditionArgument() {
    timeout.setIf( true );

    assertThat( timeout.isExpired() ).isFalse();
  }

  @Test
  public void setIfWithFalseAsConditionArgument() {
    timeout.setIf( false );

    assertThat( timeout.isExpired() ).isTrue();
  }

  @Test
  public void expire() {
    timeout.set();

    sleep( EXPIRATION_TIME * 2 );

    assertThat( timeout.isExpired() ).isTrue();
  }

  @Test
  public void setWithinExpirationTime() {
    timeout.set();
    sleep( EXPIRATION_TIME / 2 );
    timeout.set();
    sleep( EXPIRATION_TIME * 4 / 5 );

    assertThat( timeout.isExpired() ).isFalse();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsTimeUnitArgument() {
    new Timeout( 1L, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithExpirationTimeArgumentBelowLowerBound() {
    new Timeout( Timeout.LOWER_BOUND - 1L, MILLIS );
  }

  private static void sleep( long millis ) {
    try {
      Thread.sleep( millis );
    } catch( InterruptedException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

}