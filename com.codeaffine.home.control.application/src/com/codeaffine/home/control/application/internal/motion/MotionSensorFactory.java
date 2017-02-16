package com.codeaffine.home.control.application.internal.motion;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.entity.AllocationProvider.AllocationControlFactory;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.item.ContactItem;


public class MotionSensorFactory implements EntityFactory<MotionSensor, MotionSensorDefinition> {

  private final AllocationControlFactory allocationControlFactory;
  private final Registry registry;

  public MotionSensorFactory( Registry registry, AllocationControlFactory allocationControlFactory ) {
    this.registry = registry;
    this.allocationControlFactory = allocationControlFactory;
  }

  @Override
  public MotionSensor create( MotionSensorDefinition definition ) {
    ContactItem item = registry.getItem( definition.toString(), ContactItem.class );
    return new MotionSensorImpl( definition, item, allocationControlFactory );
  }
}
