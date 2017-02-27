package com.codeaffine.home.control.application.lamp;

import static com.codeaffine.home.control.application.internal.lamp.LampItemHelper.*;
import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.test.LoggerHelper.stubLoggerFactory;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.internal.lamp.LampImpl;
import com.codeaffine.home.control.application.lamp.LampProvider;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class LampProviderTest {

  private static final Percent BRIGHTNESS = P_020;
  private static final Percent COLOR_TEMPERATUR = P_010;
  private static final PercentType BRIGHTNESS_OF_ITEM = new PercentType( BRIGHTNESS.intValue() );
  private static final PercentType COLOR_TEMPERATUR_OF_Item = new PercentType( COLOR_TEMPERATUR.intValue() );

  private DimmerItem colorTemperatureItem;
  private DimmerItem brightnessItem;
  private LampProvider provider;
  private SwitchItem onOffItem;
  private Registry registry;

  @Before
  public void setUp() {
    onOffItem = stubItem( SwitchItem.class );
    colorTemperatureItem = stubItem( DimmerItem.class );
    brightnessItem = stubItem( DimmerItem.class );
    registry = stubRegistry( onOffItem, brightnessItem, colorTemperatureItem );
    provider = new LampProvider( registry, stubLoggerFactory() );
  }

  @Test
  public void findAll() {
    Collection<Lamp> actual = provider.findAll();

    assertThat( actual ).hasSize( LampDefinition.values().length );
  }

  @Test
  public void findByDefinition() {
    Lamp bulb = provider.findByDefinition( BathRoomCeiling );
    bulb.setColorTemperature( COLOR_TEMPERATUR );
    bulb.setBrightness( BRIGHTNESS );
    bulb.setOnOffStatus( ON );

    verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATUR_OF_Item );
    verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( onOffItem ).updateStatus( OnOffType.ON );
    assertThat( bulb.getDefinition() ).isSameAs( BathRoomCeiling );
  }

  @Test
  public void dispose() {
    provider.dispose();

    assertThat( provider.findAll() ).isEmpty();
    assertThat( provider.findByDefinition( BathRoomCeiling ) ).isNull();
  }

  @Test
    @SuppressWarnings("unchecked")
    public void ensureLampStatesIfIntegrityWasCorrupted() {
      provider.findAll().stream().forEach( bulb -> setStatus( bulb, OFF, BRIGHTNESS, COLOR_TEMPERATUR ) );
      reset( onOffItem, brightnessItem, colorTemperatureItem );

      provider.ensureLampStates();

      verify( onOffItem, times( provider.findAll().size() ) ).updateStatus( OnOffType.ON );
      verify( brightnessItem, times( provider.findAll().size() ) ).updateStatus( PercentType.ZERO );
      verify( colorTemperatureItem, atLeast( 1 ) /* same item for all bulbs */ ).updateStatus( COLOR_TEMPERATUR_OF_Item );
    }

  @Test
    public void ensureLampStates() {
      provider.findAll().stream().forEach( bulb -> ( ( LampImpl )bulb ).setUpdateTimestamp( getExpiredTimestamp() ) );

      provider.ensureLampStates();

      verify( onOffItem, never() ).setStatus( OnOffType.ON );
      verify( brightnessItem, never() ).setStatus( BRIGHTNESS_OF_ITEM );
      verify( colorTemperatureItem, never() ).setStatus( COLOR_TEMPERATUR_OF_Item );
    }

  private static void setStatus(
    Lamp bulb, OnOff switchStatus, Percent brightness, Percent colorTemperatur )
  {
    bulb.setOnOffStatus( OnOff.ON );
    bulb.setColorTemperature( colorTemperatur );
    bulb.setBrightness( brightness );
    bulb.setOnOffStatus( switchStatus );
    ( ( LampImpl )bulb ).setUpdateTimestamp( getExpiredTimestamp() );
  }

  private static LocalDateTime getExpiredTimestamp() {
    return now().minusDays( 1 );
  }
}