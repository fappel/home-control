package com.codeaffine.home.control.status.internal.sensor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition;

public class ActivationSensorFactory implements EntityFactory<ActivationSensor, ActivationSensorDefinition> {

  private final SensorControlFactory sensorControlFactory;
  private final Registry registry;

  public ActivationSensorFactory( Registry registry, SensorControlFactory sensorControlFactory ) {
    verifyNotNull( sensorControlFactory, "sensorControlFactory" );
    verifyNotNull( registry, "registry" );

    this.sensorControlFactory = sensorControlFactory;
    this.registry = registry;
  }

  @Override
  public ActivationSensor create( ActivationSensorDefinition definition ) {
    verifyNotNull( definition, "definition" );

    SwitchItem item = registry.getItem( definition.toString(), SwitchItem.class );
    return new ActivationSensorImpl( definition, item, sensorControlFactory );
  }
}
