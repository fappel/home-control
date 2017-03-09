package com.codeaffine.home.control.application.motion;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.motion.MotionSensorProvider;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControlFactory;
import com.codeaffine.home.control.item.SwitchItem;

public class MotionSensorProviderTest {

  @Test
  public void getStreamOfDefinitions() {
    Registry registry = stubRegistry();
    SensorControlFactory sensorControlFactory = mock( SensorControlFactory.class );
    MotionSensorProvider provider = new MotionSensorProvider( registry, sensorControlFactory );

    Stream<MotionSensorDefinition> actuals = provider.getStreamOfDefinitions();

    assertThat( actuals.collect( toSet() ) )
      .hasSize( MotionSensorDefinition.values().length )
      .contains( MotionSensorDefinition.values() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new MotionSensorProvider( null, mock( SensorControlFactory.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSensorControlFactoryArgument() {
    new MotionSensorProvider( mock( Registry.class ), null );
  }

  private static Registry stubRegistry() {
    Registry result = mock( Registry.class );
    SwitchItem switchItem = mock( SwitchItem.class );
    when( result.getItem( anyString(), eq( SwitchItem.class ) ) ).thenReturn( switchItem );
    return result;
  }
}