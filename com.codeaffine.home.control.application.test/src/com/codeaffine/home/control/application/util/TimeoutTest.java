package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.test.util.thread.ThreadHelper.sleep;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
  public void executeIfExpired() {
    timeout.set();

    sleep( EXPIRATION_TIME * 2 );
    Runnable command = mock( Runnable.class );
    timeout.executeIfExpired( command );

    verify( command ).run();
  }

  @Test
  public void executeIfExpiredIfNotExpired() {
    timeout.set();

    Runnable command = mock( Runnable.class );
    timeout.executeIfExpired( command );

    verify( command, never() ).run();
  }

  @Test
  public void executeIfNotExpired() {
    timeout.set();

    Runnable command = mock( Runnable.class );
    timeout.executeIfNotExpired( command );

    verify( command ).run();
  }

  @Test
  public void executeIfNotExpiredIfExpired() {
    timeout.set();

    sleep( EXPIRATION_TIME * 2 );
    Runnable command = mock( Runnable.class );
    timeout.executeIfNotExpired( command );

    verify( command, never() ).run();
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

  @Test( expected = IllegalArgumentException.class )
  public void executeIfNotExpiredWithNullAsCommandArgument() {
    timeout.executeIfNotExpired( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void executeIfExpiredWithNullAsCommandArgument() {
    timeout.executeIfExpired( null );
  }
}