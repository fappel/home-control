package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.home.control.test.util.entity.SensorEventAssert.assertThat;
import static com.codeaffine.home.control.test.util.entity.SensorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;

@SuppressWarnings("unchecked")
public class SensorControlImplTest {

  private final static Object SENSOR_STATUS = new Object();
  private static final String NAME = "name";

  private Entity<EntityDefinition<?>> affected;
  private SensorControl control;
  private EventBus eventBus;
  private Sensor sensor;

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    sensor = stubSensor( NAME );
    affected = mock( Entity.class );
    control = new SensorControlImpl( sensor, stubEventFactory( affected, sensor, SENSOR_STATUS ), eventBus );
  }

  @Test
  public void notifyAboutSensorStatusChange() {
    control.registerAffected( affected );

    control.notifyAboutSensorStatusChange( SENSOR_STATUS );
    SensorEvent<?> actual = captureSensorEvent( eventBus );

    assertThat( actual )
      .hasAffected( affected )
      .hasSensor( sensor )
      .hasSensorStatus( SENSOR_STATUS );
  }

  @Test
  public void notifyAboutSensorStatusChangeWithoutRegisteredAffected() {
    control.notifyAboutSensorStatusChange( SENSOR_STATUS );

    verify( eventBus, never() ).post( any( SensorEvent.class ) );
  }

  @Test
  public void notifyAboutSensorStatusChangeAfterUnregisteringAffected() {
    control.registerAffected( affected );

    control.unregisterAffected( affected );
    control.notifyAboutSensorStatusChange( SENSOR_STATUS );

    verify( eventBus, never() ).post( any( SensorEvent.class ) );
  }

  @Test
  public void getName() {
    String actual = control.getName();

    assertThat( actual ).isEqualTo( NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void notifyAboutSensorStatusChangeWithNullAsSensorStatusArgument() {
    control.notifyAboutSensorStatusChange( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerAffectedWithNullAsAffectedArgument() {
    control.registerAffected( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterAffectedWithNullAsAffectedArgument() {
    control.unregisterAffected( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSensorArgument() {
    new SensorControlImpl( null, stubEventFactory( affected, sensor, SENSOR_STATUS ), eventBus );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new SensorControlImpl( sensor, stubEventFactory( affected, sensor, SENSOR_STATUS ), null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventFactoryArgument() {
    new SensorControlImpl( sensor, null, eventBus );
  }
}