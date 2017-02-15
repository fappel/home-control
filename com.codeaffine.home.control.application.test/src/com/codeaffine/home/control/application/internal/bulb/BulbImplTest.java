package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.internal.bulb.BulbItemHelper.stubItem;
import static com.codeaffine.home.control.type.OnOffType.ON;
import static com.codeaffine.home.control.type.PercentType.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbImplTest {

  private static final PercentType BRIGHTNESS = new PercentType( 20 );
  private static final PercentType COLOR_TEMPERATUR = new PercentType( 10 );

  private DimmerItem colorTemperatureItem;
  private DimmerItem brightnessItem;
  private SwitchItem onOffItem;
  private BulbImpl bulb;

  @Before
  public void setUp() {
    onOffItem = stubItem( SwitchItem.class );
    brightnessItem = stubItem( DimmerItem.class );
    colorTemperatureItem = stubItem( DimmerItem.class );
    bulb = new BulbImpl( BathRoomCeiling, onOffItem, brightnessItem, colorTemperatureItem );
  }

  @Test
  public void initialization() {
    assertThat( bulb.getOnOffStatus() ).isNotPresent();
    assertThat( bulb.getBrightness() ).isNotPresent();
    assertThat( bulb.getColorTemperature() ).isNotPresent();
    assertThat( bulb.getDefinition() ).isSameAs( BathRoomCeiling );
  }

  @Test
  public void ensure() {
    bulb.ensure();

    verify( onOffItem, never() ).setStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).setStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).setStatus( any( PercentType.class ) );
  }

  @Test
  public void ensureIfBrightnessItemChanged() {
    bulb.setBrightness( BRIGHTNESS );
    when( brightnessItem.getStatus() ).thenReturn( Optional.of( ZERO ) );

    bulb.ensure();

    verify( onOffItem ).setStatus( ON );
    verify( brightnessItem, times( 2 ) ).setStatus( BRIGHTNESS );
    verify( colorTemperatureItem, never() ).setStatus( any( PercentType.class ) );
  }

  @Test
  public void ensureIfBrightnessItemChangedAfterInitialization() {
    when( brightnessItem.getStatus() ).thenReturn( Optional.of( BRIGHTNESS ) );

    bulb.ensure();

    verify( onOffItem ).setStatus( ON );
    verify( brightnessItem, never() ).setStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).setStatus( any( PercentType.class ) );
  }

  @Test
  public void ensureIfColorTemperatureItemChanged() {
    bulb.setColorTemperature( COLOR_TEMPERATUR );
    when( colorTemperatureItem.getStatus() ).thenReturn( Optional.of( ZERO ) );

    bulb.ensure();

    verify( onOffItem ).setStatus( ON );
    verify( brightnessItem, never() ).setStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, times( 2 ) ).setStatus( COLOR_TEMPERATUR );
  }

  @Test
  public void ensureIfColorTemperatureItemChangedAfterInitialization() {
    when( colorTemperatureItem.getStatus() ).thenReturn( Optional.of( COLOR_TEMPERATUR ) );

    bulb.ensure();

    verify( onOffItem ).setStatus( ON );
    verify( brightnessItem, never() ).setStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).setStatus( any( PercentType.class ) );
  }

  @Test
  public void ensureIfOnOffItemChanged() {
    bulb.setOnOffStatus( ON );
    when( onOffItem.getStatus() ).thenReturn( Optional.of( OnOffType.OFF ) );

    bulb.ensure();

    verify( onOffItem, times( 2 ) ).setStatus( ON );
    verify( brightnessItem, never() ).setStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).setStatus( any( PercentType.class ) );
  }

  @Test
  public void ensureIfOnOffItemChangedAfterInitialization() {
    when( onOffItem.getStatus() ).thenReturn( Optional.of( OnOffType.OFF ) );

    bulb.ensure();

    verify( onOffItem ).setStatus( ON );
    verify( brightnessItem, never() ).setStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).setStatus( any( PercentType.class ) );
  }

  @Test
  public void toStringImplementation() {
    bulb.setColorTemperature( COLOR_TEMPERATUR );
    bulb.setBrightness( BRIGHTNESS );
    bulb.setOnOffStatus( ON );

    String actual = bulb.toString();

    assertThat( actual ).contains( BathRoomCeiling.toString(),
                                   COLOR_TEMPERATUR.toString(),
                                   BRIGHTNESS.toString(),
                                   ON.toString() );
  }
}