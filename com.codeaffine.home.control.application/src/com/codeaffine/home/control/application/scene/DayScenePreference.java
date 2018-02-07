package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.application.operation.LampTimeoutModus;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface DayScenePreference extends LampTimeoutModusPreference {

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
