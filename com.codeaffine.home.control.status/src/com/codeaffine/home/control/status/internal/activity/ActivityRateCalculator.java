package com.codeaffine.home.control.status.internal.activity;

import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.status.model.SectionProvider.Section;

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