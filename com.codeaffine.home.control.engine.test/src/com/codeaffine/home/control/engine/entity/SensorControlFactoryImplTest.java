package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.home.control.test.util.entity.SensorEventAssert.assertThat;
import static com.codeaffine.home.control.test.util.entity.SensorHelper.*;
import static org.mockito.Mockito.mock;

import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.event.EventBus;

public class SensorControlFactoryImplTest {

  private static final Object SENSOR_STATUS = new Object();

  private SensorControlFactoryImpl factory;
  private EventBus eventBus;

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    factory = new SensorControlFactoryImpl( eventBus );
  }

  @Test
  @SuppressWarnings({ "unchecked" })
  public void create() {
    Entity<?> affected = mock( Entity.class );
    Sensor sensor = mock( Sensor.class );

    SensorControl control = factory.create( sensor, stubEventFactory( affected, sensor, SENSOR_STATUS ) );
    control.registerAffected( affected );
    control.notifyAboutSensorStatusChange( SENSOR_STATUS );

    assertThat( captureSensorEvent( eventBus ) )
      .hasSensor( sensor )
      .hasAffected( affected )
      .hasSensorStatus( SENSOR_STATUS );
  }

  @Test( expected = IllegalArgumentException.class )
  @SuppressWarnings("unchecked")
  public void createWithNullAsSensorArgument() {
    factory.create( null, mock( BiFunction.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsEventFactoryArgument() {
    factory.create( mock( Sensor.class ), null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusFactory() {
    new SensorControlFactoryImpl( null );
  }
}