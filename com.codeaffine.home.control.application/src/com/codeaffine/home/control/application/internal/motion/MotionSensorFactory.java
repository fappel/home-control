package com.codeaffine.home.control.application.internal.motion;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.item.SwitchItem;


public class MotionSensorFactory implements EntityFactory<MotionSensor, MotionSensorDefinition> {

  private final SensorControlFactory allocationControlFactory;
  private final Registry registry;

  public MotionSensorFactory( Registry registry, SensorControlFactory allocationControlFactory ) {
    this.registry = registry;
    this.allocationControlFactory = allocationControlFactory;
  }

  @Override
  public MotionSensor create( MotionSensorDefinition definition ) {
    SwitchItem item = registry.getItem( definition.toString(), SwitchItem.class );
    return new MotionSensorImpl( definition, item, allocationControlFactory );
  }
}
