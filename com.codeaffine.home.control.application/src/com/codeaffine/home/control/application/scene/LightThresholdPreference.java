package com.codeaffine.home.control.application.scene;

import java.util.Map;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;

@Preference
public interface LightThresholdPreference {
  @DefaultValue( "{DRESSING_AREA_LUX=70, LIVING_AREA_LUX=130, WORK_AREA_LUX=800, HALL_LUX=25, DINING_AREA_LUX=95, BATH_ROOM_LUX=85}" )
  Map<LightSensorDefinition, Integer> getSwitchOffThreshold();
  void setSwitchOffThreshold( Map<LightSensorDefinition, Integer> value );
  @DefaultValue( "{DRESSING_AREA_LUX=50, LIVING_AREA_LUX=80, WORK_AREA_LUX=500, HALL_LUX=5, DINING_AREA_LUX=70, BATH_ROOM_LUX=50}" )
  Map<LightSensorDefinition, Integer> getSwitchOnThreshold();
  void setSwitchOnThreshold( Map<LightSensorDefinition, Integer> value );
}
