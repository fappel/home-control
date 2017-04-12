package com.codeaffine.home.control.status.model;

import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.status.internal.sensor.LightSensorFactory;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensor;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;

public class LightSensorProvider extends BaseEntityProvider<LightSensor, LightSensorDefinition> {

  public enum LightSensorDefinition implements EntityDefinition<LightSensor> {
    COOKING_AREA_LUX,
    DINING_AREA_LUX,
    BED_LUX,
    BED_SIDE_LUX,
    DRESSING_AREA_LUX,
    WORK_AREA_LUX,
    LIVING_AREA_LUX,
    HALL_LUX,
    BATH_ROOM_LUX
  };

  public interface LightSensor extends Entity<LightSensorDefinition>, Sensor {
    int getLightValue();
  }

  public LightSensorProvider( Registry registry, SensorControlFactory sensorControlFactory ) {
    super( new LightSensorFactory( registry, sensorControlFactory ) );
  }

  @Override
  protected Stream<LightSensorDefinition> getStreamOfDefinitions() {
    return Stream.of( LightSensorDefinition.values() );
  }
}