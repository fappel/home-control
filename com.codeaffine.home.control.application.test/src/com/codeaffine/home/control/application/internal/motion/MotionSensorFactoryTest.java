package com.codeaffine.home.control.application.internal.motion;

import static com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition.bathRoomMotion1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControlFactory;
import com.codeaffine.home.control.item.SwitchItem;

public class MotionSensorFactoryTest {

  private MotionSensorFactory motionSensorFactory;

  @Before
  public void setUp() {
    SensorControlFactory sensorControlFactory = mock( SensorControlFactory.class );
    SwitchItem sensorItem = mock( SwitchItem.class );
    Registry registry = stubRegistry( sensorItem, bathRoomMotion1.toString() );
    motionSensorFactory = new MotionSensorFactory( registry, sensorControlFactory );
  }

  @Test
  public void create() {
    MotionSensor actual = motionSensorFactory.create( bathRoomMotion1 );

    assertThat( actual.getDefinition() ).isEqualTo( bathRoomMotion1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinition() {
    motionSensorFactory.create( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new MotionSensorFactory( null, mock( SensorControlFactory.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSensorControlFactoryArgument() {
    new MotionSensorFactory( mock( Registry.class ), null );
  }

  private static Registry stubRegistry( SwitchItem sensorItem, String sensorItemKey ) {
    Registry result = mock( Registry.class );
    when( result.getItem( sensorItemKey, SwitchItem.class ) ).thenReturn( sensorItem );
    return result;
  }
}