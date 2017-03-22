package com.codeaffine.home.control.application.internal.sensor;

import static com.codeaffine.home.control.type.OnOffType.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Optional;

import com.codeaffine.home.control.application.sensor.ActivationEvent;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;

public class ActivationSensorImpl implements ActivationSensor {

  private final ActivationSensorDefinition definition;
  private final SensorControl sensorControl;
  private final SwitchItem sensorItem;

  public ActivationSensorImpl(
    ActivationSensorDefinition definition, SwitchItem sensorItem, SensorControlFactory sensorControlFactory )
  {
    verifyNotNull( sensorControlFactory, "sensorControlFactory" );
    verifyNotNull( definition, "definition" );
    verifyNotNull( sensorItem, "sensorItem" );

    this.sensorControl = sensorControlFactory.create( this, ( status, affected ) -> newEvent( status, affected ) );
    this.sensorItem = sensorItem;
    this.definition = definition;
    initialize();
  }

  @Override
  public ActivationSensorDefinition getDefinition() {
    return definition;
  }

  @Override
  public boolean isEngaged() {
    return sensorItem.getStatus().orElse( OFF ).equals( ON );
  }

  @Override
  public void registerAffected( Entity<?> affected ) {
    verifyNotNull( affected, "affected" );

    sensorControl.registerAffected( affected );
  }

  @Override
  public void unregisterAffected( Entity<?> affected ) {
    verifyNotNull( affected, "affected" );

    sensorControl.unregisterAffected( affected );
  }

  private void initialize() {
    sensorItem.addChangeListener( evt -> handleEntityAllocation( evt ) );
  }

  private void handleEntityAllocation( ChangeEvent<SwitchItem, OnOffType> evt ) {
    if( mustEngage( evt ) ) {
      sensorControl.notifyAboutSensorStatusChange( OnOff.ON );
    } else {
      sensorControl.notifyAboutSensorStatusChange( OnOff.OFF );
    }
  }

  private static boolean mustEngage( ChangeEvent<SwitchItem, OnOffType> evt ) {
    return evt.getNewStatus().equals( Optional.of( ON ) );
  }

  @Override
  public String getName() {
    return getDefinition().toString();
  }

  private ActivationEvent newEvent( Object status, Entity<?>[] affected ) {
    return new ActivationEvent( this, ( OnOff )status, affected );
  }
}