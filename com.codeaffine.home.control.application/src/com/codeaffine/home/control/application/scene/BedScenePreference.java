package com.codeaffine.home.control.application.scene;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.status.type.Percent;

@Preference
public interface BedScenePreference {

  @DefaultValue( "P_040" )
  Percent getLampMinimumBrightness();
  void setLampMinimumBrightness( Percent value );
}
