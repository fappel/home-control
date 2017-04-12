package com.codeaffine.home.control.status.internal.sensor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensor;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;

public class LightSensorFactory implements EntityFactory<LightSensor, LightSensorDefinition> {

  private final SensorControlFactory sensorControlFactory;
  private final Registry registry;

  public LightSensorFactory( Registry registry, SensorControlFactory sensorControlFactory ) {
    verifyNotNull( sensorControlFactory, "sensorControlFactory" );
    verifyNotNull( registry, "registry" );

    this.sensorControlFactory = sensorControlFactory;
    this.registry = registry;
  }

  @Override
  public LightSensor create( LightSensorDefinition definition ) {
    verifyNotNull( definition, "definition" );

    NumberItem item = registry.getItem( definition.toString(), NumberItem.class );
    return new LightSensorImpl( definition, item, sensorControlFactory );
  }
}
