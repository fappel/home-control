package com.codeaffine.home.control.application.internal.activity;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition;

class ActivityRateCalculator extends RateCalculator {

  ActivityRateCalculator( Section section, ActivationTracker activityTracker ) {
    super( section, activityTracker );
  }

  @Override
  protected boolean isActive() {
    Class<ActivationSensorDefinition> sensorType = ActivationSensorDefinition.class;
    return getSection().getChildren( sensorType ).stream().anyMatch( sensor -> sensor.isEngaged() );
  }
}