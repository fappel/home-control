package com.codeaffine.home.control.application.sensor;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.item.SwitchItem;

public class ActivationSensorProviderTest {

  @Test
  public void getStreamOfDefinitions() {
    Registry registry = stubRegistry();
    SensorControlFactory sensorControlFactory = mock( SensorControlFactory.class );
    ActivationSensorProvider provider = new ActivationSensorProvider( registry, sensorControlFactory );

    Stream<ActivationSensorDefinition> actuals = provider.getStreamOfDefinitions();

    assertThat( actuals.collect( toSet() ) )
      .hasSize( ActivationSensorDefinition.values().length )
      .contains( ActivationSensorDefinition.values() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new ActivationSensorProvider( null, mock( SensorControlFactory.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSensorControlFactoryArgument() {
    new ActivationSensorProvider( mock( Registry.class ), null );
  }

  private static Registry stubRegistry() {
    Registry result = mock( Registry.class );
    SwitchItem switchItem = mock( SwitchItem.class );
    when( result.getItem( anyString(), eq( SwitchItem.class ) ) ).thenReturn( switchItem );
    return result;
  }
}