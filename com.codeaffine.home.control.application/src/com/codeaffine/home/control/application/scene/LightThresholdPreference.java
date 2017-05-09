package com.codeaffine.home.control.application.scene;

import java.util.Map;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;

@Preference
public interface LightThresholdPreference {
  @DefaultValue( "{DRESSING_AREA_LUX=70, LIVING_AREA_LUX=130, WORK_AREA_LUX=800, HALL_LUX=20, DINING_AREA_LUX=95, BATH_ROOM_LUX=85}" )
  Map<LightSensorDefinition, Integer> getThreshold();
  void setThreshold( Map<LightSensorDefinition, Integer> value );
}
