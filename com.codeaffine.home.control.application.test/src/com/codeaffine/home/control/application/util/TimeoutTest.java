package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.util.TimeoutPreferenceHelper.stubPreference;
import static com.codeaffine.home.control.test.util.thread.ThreadHelper.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

public class TimeoutTest {

  private static final ChronoUnit TIME_UNIT = ChronoUnit.MILLIS;
  private static final long EXPIRATION_TIME = 20L;

  private TimeoutPreference preference;
  private Timeout timeout;

  @Before
  public void setUp() {
    preference = stubPreference( EXPIRATION_TIME, TIME_UNIT );
    timeout = new Timeout( preference );
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
  public void expireIfPreferenceSettingsChange() {
    stubPreference( preference, EXPIRATION_TIME * 4, TIME_UNIT );
    timeout.set();

    sleep( EXPIRATION_TIME * 2 );
    boolean expiredWithOldSetting = timeout.isExpired();
    sleep( EXPIRATION_TIME * 8 );
    boolean expiredWithNewSetting = timeout.isExpired();

    assertThat( expiredWithOldSetting ).isFalse();
    assertThat( expiredWithNewSetting ).isTrue();
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
    Runnable command = mock( Runnable.class );
    timeout.set();

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
  public void executeIfNotExpiredWithNullAsCommandArgument() {
    timeout.executeIfNotExpired( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void executeIfExpiredWithNullAsCommandArgument() {
    timeout.executeIfExpired( null );
  }
}