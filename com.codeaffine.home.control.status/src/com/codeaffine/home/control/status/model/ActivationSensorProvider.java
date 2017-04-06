package com.codeaffine.home.control.status.model;

import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.status.internal.sensor.ActivationSensorFactory;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition;

public class ActivationSensorProvider extends BaseEntityProvider<ActivationSensor, ActivationSensorDefinition> {

  public enum ActivationSensorDefinition implements EntityDefinition<ActivationSensor> {
    COOKING_AREA_MOTION,
    DINING_AREA_MOTION,
    BED_MOTION,
    BED_SIDE_MOTION,
    DRESSING_AREA_MOTION,
    WORK_AREA_MOTION,
    LIVING_AREA_MOTION,
    HALL_MOTION,
    BATH_ROOM_MOTION
  };

  public interface ActivationSensor extends Entity<ActivationSensorDefinition>, Sensor {
    boolean isEngaged();
  }

  public ActivationSensorProvider( Registry registry, SensorControlFactory sensorControlFactory ) {
    super( new ActivationSensorFactory( registry, sensorControlFactory ) );
  }

  @Override
  protected Stream<ActivationSensorDefinition> getStreamOfDefinitions() {
    return Stream.of( ActivationSensorDefinition.values() );
  }
}