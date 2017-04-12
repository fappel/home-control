package com.codeaffine.home.control.status.internal.sensor;

import static com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition.BED_LUX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensor;

public class LightSensorFactoryTest {

  private LightSensorFactory lightSensorFactory;

  @Before
  public void setUp() {
    SensorControlFactory sensorControlFactory = mock( SensorControlFactory.class );
    NumberItem sensorItem = mock( NumberItem.class );
    Registry registry = stubRegistry( sensorItem, BED_LUX.toString() );
    lightSensorFactory = new LightSensorFactory( registry, sensorControlFactory );
  }

  @Test
  public void create() {
    LightSensor actual = lightSensorFactory.create( BED_LUX );

    assertThat( actual.getDefinition() ).isEqualTo( BED_LUX );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinition() {
    lightSensorFactory.create( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new LightSensorFactory( null, mock( SensorControlFactory.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSensorControlFactoryArgument() {
    new LightSensorFactory( mock( Registry.class ), null );
  }

  private static Registry stubRegistry( NumberItem sensorItem, String sensorItemKey ) {
    Registry result = mock( Registry.class );
    when( result.getItem( sensorItemKey, NumberItem.class ) ).thenReturn( sensorItem );
    return result;
  }
}