package com.codeaffine.home.control.application;

import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.application.internal.motion.MotionSensorFactory;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.AllocationProvider.AllocationActor;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.AllocationProvider.AllocationControlFactory;

public class MotionSensorProvider extends BaseEntityProvider<MotionSensor, MotionSensorDefinition> {

  public enum MotionSensorDefinition implements EntityDefinition<MotionSensor> {
    kitchenMotion, livingRoomMotion, bedRoomMotion, hallMotion, bathRoomMotion;
  };

  public interface MotionSensor extends Entity<MotionSensorDefinition>, AllocationActor {

  }

  public MotionSensorProvider( Registry registry, AllocationControlFactory allocationControlFactory ) {
    super( new MotionSensorFactory( registry, allocationControlFactory ) );
  }

  @Override
  protected Stream<MotionSensorDefinition> getStreamOfDefinitions() {
    return Stream.of( MotionSensorDefinition.values() );
  }
}