package com.codeaffine.home.control.status.model;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;

public class LightSensorProviderTest {

  @Test
  public void getStreamOfDefinitions() {
    Registry registry = stubRegistry();
    SensorControlFactory sensorControlFactory = mock( SensorControlFactory.class );
    LightSensorProvider provider = new LightSensorProvider( registry, sensorControlFactory );

    Stream<LightSensorDefinition> actuals = provider.getStreamOfDefinitions();

    assertThat( actuals.collect( toSet() ) )
      .hasSize( LightSensorDefinition.values().length )
      .contains( LightSensorDefinition.values() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new LightSensorProvider( null, mock( SensorControlFactory.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSensorControlFactoryArgument() {
    new LightSensorProvider( mock( Registry.class ), null );
  }

  private static Registry stubRegistry() {
    Registry result = mock( Registry.class );
    NumberItem switchItem = mock( NumberItem.class );
    when( result.getItem( anyString(), eq( NumberItem.class ) ) ).thenReturn( switchItem );
    return result;
  }
}