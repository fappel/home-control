package com.codeaffine.home.control.test.util.entity;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.function.BiFunction;

import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.event.EventBus;

public class SensorHelper {

  public static Sensor stubSensor( String name ) {
    Sensor result = mock( Sensor.class );
    when( result.getName() ).thenReturn( name );
    return result;
  }

  public static SensorControlFactory stubSensorControlFactory( SensorControl sensorControl ) {
    SensorControlFactory result = mock( SensorControlFactory.class );
    when( result.create( any(), any() ) ).thenReturn( sensorControl );
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static BiFunction stubEventFactory( Entity<?> affected, Sensor sensor, Object sensorStatus ) {
    BiFunction result = mock( BiFunction.class );
    when( result.apply( sensorStatus, new Entity[] { affected } ) )
      .thenReturn( new SensorEvent<>( sensor, sensorStatus, affected ) );
    return result;
  }

  @SuppressWarnings("rawtypes")
  public static SensorEvent<?> captureSensorEvent( EventBus eventBus ) {
    ArgumentCaptor<SensorEvent> captor = forClass( SensorEvent.class );
    verify( eventBus ).post( captor.capture() );
    return captor.getValue();
  }
}