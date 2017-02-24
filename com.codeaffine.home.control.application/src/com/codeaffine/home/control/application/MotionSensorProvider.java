package com.codeaffine.home.control.application;

import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.application.internal.motion.MotionSensorFactory;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.ZoneProvider.Sensor;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;

public class MotionSensorProvider extends BaseEntityProvider<MotionSensor, MotionSensorDefinition> {

  public enum MotionSensorDefinition implements EntityDefinition<MotionSensor> {
    kitchenMotion1, livingRoomMotion1, bedRoomMotion1, hallMotion1, bathRoomMotion1;
  };

  public interface MotionSensor extends Entity<MotionSensorDefinition>, Sensor {
    boolean isEngaged();
  }

  public MotionSensorProvider( Registry registry, SensorControlFactory sensorControlFactory ) {
    super( new MotionSensorFactory( registry, sensorControlFactory ) );
  }

  @Override
  protected Stream<MotionSensorDefinition> getStreamOfDefinitions() {
    return Stream.of( MotionSensorDefinition.values() );
  }
}