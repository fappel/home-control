package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.home.control.engine.entity.SensorEventCaptor.captureSensorEvent;
import static com.codeaffine.home.control.test.util.entity.SensorEventAssert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;

@SuppressWarnings("unchecked")
public class SensorControlImplTest {

  private final static Object SENSOR_STATUS = new Object();

  private Entity<EntityDefinition<?>> affected;
  private Entity<EntityDefinition<?>> sensor;
  private SensorControl control;
  private EventBus eventBus;

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    sensor = mock( Entity.class );
    control = new SensorControlImpl( sensor, eventBus );
    affected = mock( Entity.class );
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
    SensorEvent<?> actual = captureSensorEvent( eventBus );

    assertThat( actual )
      .hasNoAffected()
      .hasSensor( sensor )
      .hasSensorStatus( SENSOR_STATUS );
  }

  @Test
  public void notifyAboutSensorStatusChangeAfterUnregisteringAffected() {
    control.registerAffected( affected );

    control.unregisterAffected( affected );
    control.notifyAboutSensorStatusChange( SENSOR_STATUS );
    SensorEvent<?> actual = captureSensorEvent( eventBus );

    assertThat( actual )
      .hasNoAffected()
      .hasSensor( sensor )
      .hasSensorStatus( SENSOR_STATUS );
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
    new SensorControlImpl( null, eventBus );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new SensorControlImpl( sensor, null );
  }
}