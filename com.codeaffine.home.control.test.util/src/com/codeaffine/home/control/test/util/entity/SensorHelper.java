package com.codeaffine.home.control.test.util.entity;

import static org.mockito.Mockito.*;

import com.codeaffine.home.control.entity.Sensor;

public class SensorHelper {
  
  public static Sensor stubSensor( String name ) {
    Sensor result = mock( Sensor.class );
    when( result.getName() ).thenReturn( name );
    return result;
  }
}