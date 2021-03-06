package com.codeaffine.home.control.application.scene;

import java.time.temporal.ChronoUnit;

import com.codeaffine.home.control.application.operation.LampTimeoutModus;
import com.codeaffine.home.control.application.util.TimeoutPreference;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface NightScenePreference extends TimeoutPreference, LampTimeoutModusPreference {

  @Override
  @DefaultValue( "2" )
  long getExpirationTime();
  @Override
  void setExpirationTime( long value );

  @Override
  @DefaultValue( "MINUTES" )
  ChronoUnit getTimeUnit();
  @Override
  void setTimeUnit( ChronoUnit value );

  @Override
  @DefaultValue( "OFF" )
  LampTimeoutModus getLampTimeoutModusDay();
  @Override
  void setLampTimeoutModusDay( LampTimeoutModus value );

  @Override
  @DefaultValue( "ON" )
  LampTimeoutModus getLampTimeoutModusNight();
  @Override
  void setLampTimeoutModusNight( LampTimeoutModus value );

}