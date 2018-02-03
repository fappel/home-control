package com.codeaffine.home.control.application.util;

import java.time.temporal.ChronoUnit;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface TimeoutPreference {

  @DefaultValue( "1" )
  long getExpirationTime();
  void setExpirationTime( long value );

  @DefaultValue( "MINUTES" )
  ChronoUnit getTimeUnit();
  void setTimeUnit( ChronoUnit value );
}