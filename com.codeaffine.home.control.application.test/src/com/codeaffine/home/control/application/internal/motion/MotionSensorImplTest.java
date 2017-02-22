package com.codeaffine.home.control.application.internal.motion;

import static com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition.bathRoomMotion1;
import static org.mockito.Mockito.mock;

import org.junit.Before;

import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;
import com.codeaffine.home.control.item.SwitchItem;

public class MotionSensorImplTest {

  @Before
  public void setUp() {
    SwitchItem sensorSwitch = mock( SwitchItem.class );
    new MotionSensorImpl( bathRoomMotion1, sensorSwitch, mock( SensorControlFactory.class ) );
  }
}