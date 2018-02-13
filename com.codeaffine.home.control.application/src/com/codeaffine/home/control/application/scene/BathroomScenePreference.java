package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.status.type.Percent;

@Preference
public interface BathroomScenePreference {

  @DefaultValue( "P_070" )
  Percent getLampMinimumBrightness();
  void setLampMinimumBrightness( Percent value );
}
