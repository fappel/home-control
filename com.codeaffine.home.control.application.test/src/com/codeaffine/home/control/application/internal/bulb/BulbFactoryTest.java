package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.internal.bulb.BulbItemHelper.*;
import static com.codeaffine.home.control.application.test.LoggerHelper.stubLoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;

public class BulbFactoryTest {

  private BulbFactory bulbFactory;

  @Before
  public void setUp() {
    DimmerItem colorTemperatureItem = stubItem( DimmerItem.class );
    DimmerItem brightnessItem = stubItem( DimmerItem.class );
    SwitchItem onOffItem = stubItem( SwitchItem.class );
    bulbFactory = new BulbFactory( stubRegistry( onOffItem, brightnessItem, colorTemperatureItem ), stubLoggerFactory() );
  }

  @Test
  public void create() {
    Bulb bulb = bulbFactory.create( BulbDefinition.BathRoomCeiling );

    assertThat( bulb.getDefinition() ).isSameAs( BulbDefinition.BathRoomCeiling );
    assertThat( bulb.getOnOffStatus() ).isNotPresent();
    assertThat( bulb.getBrightness() ).isNotPresent();
    assertThat( bulb.getColorTemperature() ).isNotPresent();
    assertThat( bulb.getDefinition() ).isSameAs( BulbDefinition.BathRoomCeiling );
  }
}