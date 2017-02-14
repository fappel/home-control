package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.internal.bulb.BulbItemHelper.*;
import static com.codeaffine.home.control.type.OnOffType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbProviderTest {

  private static final PercentType BRIGHTNESS = new PercentType( 20 );
  private static final PercentType COLOR_TEMPERATUR = new PercentType( 10 );

  private DimmerItem colorTemperatureItem;
  private DimmerItem brightnessItem;
  private BulbProvider provider;
  private SwitchItem onOffItem;
  private Registry registry;

  @Before
  public void setUp() {
    onOffItem = stubItem( SwitchItem.class );
    colorTemperatureItem = stubItem( DimmerItem.class );
    brightnessItem = stubItem( DimmerItem.class );
    registry = stubRegistry( onOffItem, brightnessItem, colorTemperatureItem );
    provider = new BulbProvider( registry );
  }

  @Test
  public void findAll() {
    Collection<Bulb> actual = provider.findAll();

    assertThat( actual ).hasSize( BulbDefinition.values().length );
  }

  @Test
  public void findByDefinition() {
    Bulb bulb = provider.findByDefinition( BathRoomCeiling );
    bulb.setColorTemperature( COLOR_TEMPERATUR );
    bulb.setBrightness( BRIGHTNESS );
    bulb.setOnOffStatus( ON );

    verify( colorTemperatureItem ).setStatus( COLOR_TEMPERATUR );
    verify( brightnessItem ).setStatus( BRIGHTNESS );
    verify( onOffItem ).setStatus( ON );
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
  public void ensureBulbStatesIfIntegrityWasCorrupted() {
    provider.findAll().stream().forEach( bulb -> setStatus( bulb, OFF, BRIGHTNESS, COLOR_TEMPERATUR ) );
    reset( onOffItem, brightnessItem, colorTemperatureItem );

    provider.ensureBulbStates();

    verify( onOffItem, times( BulbDefinition.values().length ) ).setStatus( ON );
    verify( brightnessItem, times( BulbDefinition.values().length ) ).setStatus( BRIGHTNESS );
    verify( colorTemperatureItem, times( BulbDefinition.values().length ) ).setStatus( COLOR_TEMPERATUR );
  }

  @Test
  public void ensureBulbStates() {
    provider.ensureBulbStates();

    verify( onOffItem, never() ).setStatus( ON );
    verify( brightnessItem, never() ).setStatus( BRIGHTNESS );
    verify( colorTemperatureItem, never() ).setStatus( COLOR_TEMPERATUR );
  }

  private static void setStatus(
    Bulb bulb, OnOffType switchStatus, PercentType brightness, PercentType colorTemperatur )
  {
    bulb.setColorTemperature( colorTemperatur );
    bulb.setBrightness( brightness );
    bulb.setOnOffStatus( switchStatus );
  }
}