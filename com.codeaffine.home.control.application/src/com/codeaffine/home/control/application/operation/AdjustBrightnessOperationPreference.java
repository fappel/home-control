package com.codeaffine.home.control.application.operation;

import java.time.temporal.ChronoUnit;

import com.codeaffine.home.control.application.util.TimeoutPreference;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.status.type.Percent;

@Preference
public interface AdjustBrightnessOperationPreference extends TimeoutPreference {

  @DefaultValue( "P_050" )
  Percent getActivityThreshold();
  void setActivityThreshold( Percent value );

  @DefaultValue( "P_030" )
  Percent getBrightnessMinimumAboveThreshold();
  void setBrightnessMinimumAboveThreshold( Percent value );

  @DefaultValue( "P_001" )
  Percent getBrightnessMinimumBelowThreshold();
  void setBrightnessMinimumBelowThreshold( Percent value );

  @Override
  @DefaultValue( "3" )
  long getExpirationTime();
  @Override
  void setExpirationTime( long value );

  @Override
  @DefaultValue( "MINUTES" )
  ChronoUnit getTimeUnit();
  @Override
  void setTimeUnit( ChronoUnit value );

}
