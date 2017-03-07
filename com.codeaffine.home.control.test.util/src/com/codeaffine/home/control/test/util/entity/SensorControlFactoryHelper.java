package com.codeaffine.home.control.test.util.entity;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;

public class SensorControlFactoryHelper {

  public static SensorControlFactory stubSensorControlFactory( SensorControl sensorControl ) {
    SensorControlFactory result = mock( SensorControlFactory.class );
    when( result.create( any() ) ).thenReturn( sensorControl );
    return result;
  }
}