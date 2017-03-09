package com.codeaffine.home.control.application.internal.motion;

import static com.codeaffine.home.control.type.OnOffType.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Optional;

import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;

public class MotionSensorImpl implements MotionSensor {

  private final MotionSensorDefinition definition;
  private final SensorControl sensorControl;
  private final SwitchItem sensorItem;

  public MotionSensorImpl(
    MotionSensorDefinition definition, SwitchItem sensorItem, SensorControlFactory sensorControlFactory )
  {
    verifyNotNull( sensorControlFactory, "sensorControlFactory" );
    verifyNotNull( definition, "definition" );
    verifyNotNull( sensorItem, "sensorItem" );

    this.sensorControl = sensorControlFactory.create( this );
    this.sensorItem = sensorItem;
    this.definition = definition;
    initialize();
  }

  @Override
  public MotionSensorDefinition getDefinition() {
    return definition;
  }

  @Override
  public boolean isEngaged() {
    return sensorItem.getStatus().orElse( OFF ).equals( ON );
  }

  @Override
  public void registerAffected( Entity<?> allocable ) {
    verifyNotNull( allocable, "allocable" );

    sensorControl.registerAffected( allocable );
  }

  @Override
  public void unregisterAffected( Entity<?> allocable ) {
    verifyNotNull( allocable, "zone" );

    sensorControl.unregisterAffected( allocable );
  }

  private void initialize() {
    sensorItem.addChangeListener( evt -> handleEntityAllocation( evt ) );
  }

  private void handleEntityAllocation( ChangeEvent<SwitchItem, OnOffType> evt ) {
    if( mustEngage( evt ) ) {
      sensorControl.notifyAboutSensorStatusChange( ON );
    } else {
      sensorControl.notifyAboutSensorStatusChange( OFF );
    }
  }

  private static boolean mustEngage( ChangeEvent<SwitchItem, OnOffType> evt ) {
    return evt.getNewStatus().equals( Optional.of( ON ) );
  }
}