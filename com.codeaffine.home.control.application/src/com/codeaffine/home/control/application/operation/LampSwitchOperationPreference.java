package com.codeaffine.home.control.application.operation;

import java.time.temporal.ChronoUnit;

import com.codeaffine.home.control.application.util.TimeoutPreference;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface LampSwitchOperationPreference extends TimeoutPreference {

  @Override
  @DefaultValue( "30" )
  long getExpirationTime();
  @Override
  void setExpirationTime( long value );

  @Override
  @DefaultValue( "SECONDS" )
  ChronoUnit getTimeUnit();
  @Override
  void setTimeUnit( ChronoUnit value );
}