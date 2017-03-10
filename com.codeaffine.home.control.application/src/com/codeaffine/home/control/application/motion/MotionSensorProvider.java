package com.codeaffine.home.control.application.motion;

import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.internal.motion.MotionSensorFactory;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.entity.SensorEvent;

public class MotionSensorProvider extends BaseEntityProvider<MotionSensor, MotionSensorDefinition> {

  public enum MotionSensorDefinition implements EntityDefinition<MotionSensor> {
    COOKING_AREA_MOTION,
    DINING_AREA_MOTION,
    BED_MOTION,
    DRESSING_AREA_MOTION,
    WORK_AREA_MOTION,
    LIVING_AREA_MOTION,
    HALL_MOTION,
    BATH_ROOM_MOTION,

    kitchenMotion1, livingRoomMotion1, bedRoomMotion1, hallMotion1, bathRoomMotion1;
  };

  public interface MotionSensor extends Entity<MotionSensorDefinition>, Sensor {
    boolean isEngaged();
  }

  public static class MotionSensorEvent extends SensorEvent<OnOff> {

    @SafeVarargs
    public MotionSensorEvent( Sensor sensor, OnOff sensorStatus, Entity<?> ... affected ) {
      super( sensor, sensorStatus, affected );
    }
  }

  public MotionSensorProvider( Registry registry, SensorControlFactory sensorControlFactory ) {
    super( new MotionSensorFactory( registry, sensorControlFactory ) );
  }

  @Override
  protected Stream<MotionSensorDefinition> getStreamOfDefinitions() {
    return Stream.of( MotionSensorDefinition.values() );
  }
}