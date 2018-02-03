package com.codeaffine.home.control.application.util;

import static org.mockito.Mockito.*;

import java.time.temporal.ChronoUnit;

public class TimeoutPreferenceHelper {

  public static TimeoutPreference stubPreference( long expirationTime, ChronoUnit timeUnit ) {
    return stubPreference( expirationTime, timeUnit, TimeoutPreference.class );
  }

  public static <T extends TimeoutPreference> T stubPreference(
    long expirationTime, ChronoUnit timeUnit, Class<T> type )
  {
    T result = mock( type );
    stubPreference( result, expirationTime, timeUnit );
    return result;
  }

  public static void stubPreference( TimeoutPreference preference, long expirationTime, ChronoUnit timeUnit ) {
    when( preference.getExpirationTime() ).thenReturn( expirationTime );
    when( preference.getTimeUnit() ).thenReturn( timeUnit );
  }
}