package com.codeaffine.home.control.status.internal.sensor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Optional;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.status.model.LightEvent;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensor;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;
import com.codeaffine.home.control.type.DecimalType;

class LightSensorImpl implements LightSensor {

  static final DecimalType INITIAL_LIGHT_VALUE = DecimalType.ZERO;

  private final LightSensorDefinition definition;
  private final SensorControl sensorControl;
  private final NumberItem sensorItem;

  LightSensorImpl(
    LightSensorDefinition definition, NumberItem sensorItem, SensorControlFactory sensorControlFactory )
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
  public LightSensorDefinition getDefinition() {
    return definition;
  }

  @Override
  public int getLightValue() {
    return toIntValue( sensorItem.getStatus() );
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
    sensorItem.addChangeListener( evt -> handleEntityMapping( evt ) );
  }

  private void handleEntityMapping( ChangeEvent<NumberItem, DecimalType> evt ) {
    sensorControl.notifyAboutSensorStatusChange( Integer.valueOf( toIntValue( evt.getNewStatus() ) ) );
  }

  @Override
  public String getName() {
    return getDefinition().toString();
  }

  private LightEvent newEvent( Object status, Entity<?>[] affected ) {
    return new LightEvent( this, ( Integer )status, affected );
  }

  private static int toIntValue( Optional<DecimalType> status ) {
    return status.orElse( INITIAL_LIGHT_VALUE ).intValue();
  }
}