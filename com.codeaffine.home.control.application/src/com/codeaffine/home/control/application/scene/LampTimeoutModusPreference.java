package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.application.operation.LampTimeoutModus;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface LampTimeoutModusPreference {

  @DefaultValue( "OFF" )
  LampTimeoutModus getLampTimeoutModusDay();
  void setLampTimeoutModusDay( LampTimeoutModus value );

  @DefaultValue( "ON" )
  LampTimeoutModus getLampTimeoutModusNight();
  void setLampTimeoutModusNight( LampTimeoutModus value );

}
