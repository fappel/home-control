package com.codeaffine.home.control.application.internal.lamp;

import static com.codeaffine.home.control.application.internal.lamp.LampItemHelper.*;
import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.test.LoggerHelper.stubLoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.internal.lamp.LampFactory;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.LoggerFactory;

public class LampFactoryTest {

  private LampFactory lampFactory;

  @Before
  public void setUp() {
    DimmerItem colorTemperatureItem = stubItem( DimmerItem.class );
    DimmerItem brightnessItem = stubItem( DimmerItem.class );
    SwitchItem onOffItem = stubItem( SwitchItem.class );
    LoggerFactory loggerFactory = stubLoggerFactory();
    lampFactory = new LampFactory( stubRegistry( onOffItem, brightnessItem, colorTemperatureItem ), loggerFactory );
  }

  @Test
  public void create() {
    Lamp actual = lampFactory.create( BathRoomCeiling );

    assertThat( actual.getDefinition() ).isSameAs( BathRoomCeiling );
    assertThat( actual.getOnOffStatus() ).isSameAs( OnOff.OFF );
    assertThat( actual.getBrightness() ).isSameAs( Percent.P_100 );
    assertThat( actual.getColorTemperature() ).isSameAs( Percent.P_000 );
    assertThat( actual.getDefinition() ).isSameAs( BathRoomCeiling );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinitionArgument() {
    lampFactory.create( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new LampFactory( null, stubLoggerFactory() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new LampFactory( mock( Registry.class ), null );
  }
}