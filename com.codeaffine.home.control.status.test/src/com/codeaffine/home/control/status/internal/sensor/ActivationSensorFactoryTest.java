package com.codeaffine.home.control.status.internal.sensor;

import static com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition.BATH_ROOM_MOTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.status.internal.sensor.ActivationSensorFactory;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;

public class ActivationSensorFactoryTest {

  private ActivationSensorFactory motionSensorFactory;

  @Before
  public void setUp() {
    SensorControlFactory sensorControlFactory = mock( SensorControlFactory.class );
    SwitchItem sensorItem = mock( SwitchItem.class );
    Registry registry = stubRegistry( sensorItem, BATH_ROOM_MOTION.toString() );
    motionSensorFactory = new ActivationSensorFactory( registry, sensorControlFactory );
  }

  @Test
  public void create() {
    ActivationSensor actual = motionSensorFactory.create( BATH_ROOM_MOTION );

    assertThat( actual.getDefinition() ).isEqualTo( BATH_ROOM_MOTION );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinition() {
    motionSensorFactory.create( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new ActivationSensorFactory( null, mock( SensorControlFactory.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSensorControlFactoryArgument() {
    new ActivationSensorFactory( mock( Registry.class ), null );
  }

  private static Registry stubRegistry( SwitchItem sensorItem, String sensorItemKey ) {
    Registry result = mock( Registry.class );
    when( result.getItem( sensorItemKey, SwitchItem.class ) ).thenReturn( sensorItem );
    return result;
  }
}