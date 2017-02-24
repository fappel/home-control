package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.internal.bulb.BulbItemHelper.*;
import static com.codeaffine.home.control.application.test.LoggerHelper.stubLoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.LoggerFactory;

public class BulbFactoryTest {

  private BulbFactory bulbFactory;

  @Before
  public void setUp() {
    DimmerItem colorTemperatureItem = stubItem( DimmerItem.class );
    DimmerItem brightnessItem = stubItem( DimmerItem.class );
    SwitchItem onOffItem = stubItem( SwitchItem.class );
    LoggerFactory loggerFactory = stubLoggerFactory();
    bulbFactory = new BulbFactory( stubRegistry( onOffItem, brightnessItem, colorTemperatureItem ), loggerFactory );
  }

  @Test
  public void create() {
    Bulb bulb = bulbFactory.create( BathRoomCeiling );

    assertThat( bulb.getDefinition() ).isSameAs( BathRoomCeiling );
    assertThat( bulb.getOnOffStatus() ).isNotPresent();
    assertThat( bulb.getBrightness() ).isNotPresent();
    assertThat( bulb.getColorTemperature() ).isNotPresent();
    assertThat( bulb.getDefinition() ).isSameAs( BathRoomCeiling );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinitionArgument() {
    bulbFactory.create( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsRegistryArgument() {
    new BulbFactory( null, stubLoggerFactory() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new BulbFactory( mock( Registry.class ), null );
  }
}