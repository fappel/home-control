package com.codeaffine.home.control.application.internal.lamp;

import static com.codeaffine.home.control.application.internal.lamp.LampItemHelper.stubItem;
import static com.codeaffine.home.control.application.internal.lamp.LampImpl.*;
import static com.codeaffine.home.control.application.internal.lamp.Messages.*;
import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.type.PercentType.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.application.internal.lamp.LampImpl;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class LampImplTest {

  private static final Percent BRIGHTNESS = Percent.P_020;
  private static final Percent COLOR_TEMPERATURE = Percent.P_010;
  private static final PercentType BRIGHTNESS_OF_ITEM = new PercentType( BRIGHTNESS.intValue() );
  private static final PercentType COLOR_TEMPERATURE_OF_ITEM = new PercentType( COLOR_TEMPERATURE.intValue() );

  private DimmerItem colorTemperatureItem;
  private DimmerItem brightnessItem;
  private SwitchItem onOffItem;
  private Logger logger;
  private LampImpl lamp;

  @Before
  public void setUp() {
    onOffItem = stubItem( SwitchItem.class );
    brightnessItem = stubItem( DimmerItem.class );
    colorTemperatureItem = stubItem( DimmerItem.class );
    logger = mock( Logger.class );
    lamp = new LampImpl( BathRoomCeiling, onOffItem, brightnessItem, colorTemperatureItem, logger );
  }

  @Test
  public void initialization() {
    assertThat( lamp.getOnOffStatus() ).isSameAs( OnOff.OFF );
    assertThat( lamp.getBrightness() ).isSameAs( Percent.P_100 );
    assertThat( lamp.getColorTemperature() ).isSameAs( Percent.P_000 );
    assertThat( lamp.getDefinition() ).isSameAs( BathRoomCeiling );
  }

  @Test
  public void setOnOffStatusToOn() {
    lamp.setBrightness( BRIGHTNESS );
    lamp.setColorTemperature( COLOR_TEMPERATURE );

    lamp.setOnOffStatus( OnOff.ON );

    InOrder order = inOrder( onOffItem, brightnessItem, colorTemperatureItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.ON );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    order.verify( logger ).info( LAMP_SWITCH_PATTERN, lamp.getDefinition(), OnOff.ON );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void setOnOffStatusToOnIfBrightnessIsNotPresent() {
    lamp.setColorTemperature( COLOR_TEMPERATURE );

    lamp.setOnOffStatus( OnOff.ON );

    InOrder order = inOrder( onOffItem, colorTemperatureItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.ON );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    order.verify( logger ).info( LAMP_SWITCH_PATTERN, lamp.getDefinition(), OnOff.ON );
    order.verifyNoMoreInteractions();
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void setOnOffStatusToOnIfColorTemperatureIsNotPresent() {
    lamp.setBrightness( BRIGHTNESS );

    lamp.setOnOffStatus( OnOff.ON );

    InOrder order = inOrder( onOffItem, brightnessItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.ON );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    order.verify( logger ).info( LAMP_SWITCH_PATTERN, lamp.getDefinition(), OnOff.ON );
    order.verifyNoMoreInteractions();
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void setOnOffStatusToOnIfAlreadySwitchedOn() {
    lamp.setBrightness( BRIGHTNESS );
    lamp.setColorTemperature( COLOR_TEMPERATURE );
    stubOnOffItemWithStatusOn();

    lamp.setOnOffStatus( OnOff.ON );

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( eq( LAMP_SWITCH_PATTERN ), eq( lamp.getDefinition() ), any( OnOffType.class ) );
  }

  @Test
  public void setOnOffStatusToOff() {
    lamp.setBrightness( BRIGHTNESS );
    lamp.setColorTemperature( COLOR_TEMPERATURE );

    lamp.setOnOffStatus( OnOff.OFF );

    InOrder order = inOrder( onOffItem, brightnessItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.OFF );
    order.verify( brightnessItem ).updateStatus( ZERO );
    order.verify( logger ).info( LAMP_SWITCH_PATTERN, lamp.getDefinition(), OnOff.OFF );
    order.verifyNoMoreInteractions();
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void getOnOffStatus() {
    stubOnOffItemWithStatusOn();

    OnOff actual = lamp.getOnOffStatus();

    assertThat( actual ).isSameAs( OnOff.ON );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setOnOffStatusWithNullAsArgument() {
    lamp.setOnOffStatus( null );
  }

  @Test
  public void setBrightness() {
    stubOnOffItemWithStatusOn();

    lamp.setBrightness( BRIGHTNESS );

    InOrder order = inOrder( brightnessItem, logger );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    order.verify( logger ).info( LAMP_SET_BRIGHTNESS_PATTERN, lamp.getDefinition(), BRIGHTNESS );
  }

  @Test
  public void setBrightnessIfValueIsAlreadySet() {
    stubOnOffItemWithStatusOn();
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );

    lamp.setBrightness( BRIGHTNESS );

    verify( brightnessItem, never() ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( logger, never() ).info( LAMP_SET_BRIGHTNESS_PATTERN, lamp.getDefinition(), BRIGHTNESS );
  }

  @Test
  public void getBrightnessIfLampIsSwitchedOn() {
    stubOnOffItemWithStatusOn();
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );

    Percent actual = lamp.getBrightness();

    assertThat( actual ).isSameAs( BRIGHTNESS );
  }

  @Test
  public void getBrightnessIfLampIsNotSwitchedOn() {
    lamp.setBrightness( BRIGHTNESS );
    Percent actual = lamp.getBrightness();

    assertThat( actual ).isSameAs( BRIGHTNESS );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setBrightnessWithNullAsArgument() {
    lamp.setBrightness( null );
  }

  @Test
  public void setColorTemperature() {
    stubOnOffItemWithStatusOn();

    lamp.setColorTemperature( COLOR_TEMPERATURE );

    InOrder order = inOrder( colorTemperatureItem, logger );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    order.verify( logger ).info( LAMP_SET_COLOR_TEMPERATURE_PATTERN, lamp.getDefinition(), COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfValueIsAlreadySet() {
    stubOnOffItemWithStatusOn();
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );

    lamp.setColorTemperature( COLOR_TEMPERATURE );

    verify( colorTemperatureItem, never() ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger, never() ).info( LAMP_SET_COLOR_TEMPERATURE_PATTERN, lamp.getDefinition(), COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfLampIsSwitchedOn() {
    stubOnOffItemWithStatusOn();
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );

    Percent actual = lamp.getColorTemperature();

    assertThat( actual ).isSameAs( COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfLampIsNotSwitchedOn() {
    lamp.setColorTemperature( COLOR_TEMPERATURE );
    Percent actual = lamp.getColorTemperature();

    assertThat( actual ).isSameAs( COLOR_TEMPERATURE );
  }

  @Test(expected = IllegalArgumentException.class)
  public void setColorTemperatureWithNullAsArgument() {
    lamp.setColorTemperature( null );
  }

  @Test
  public void ensureStatusIntegrity() {
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( DEFAULT_ON_OFF_STATE_INTERNAL );
    verify( brightnessItem ).updateStatus( DEFAULT_BRIGHTNESS_INTERNAL );
    verify( colorTemperatureItem ).updateStatus( DEFAULT_COLOR_TEMPERATURE_INTERNAL );
    verify( logger, never() ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
    verify( logger ).info( LAMP_SET_BRIGHTNESS_PATTERN, lamp.getDefinition(), DEFAULT_BRIGHTNESS );
    verify( logger ).info( LAMP_SET_COLOR_TEMPERATURE_PATTERN, lamp.getDefinition(), DEFAULT_COLOR_TEMPERATURE );
    verify( logger ).info( LAMP_SWITCH_PATTERN, lamp.getDefinition(), DEFAULT_ON_OFF_STATE );
  }

  @Test
  public void ensureStatusIntegrityIfLampIsInitialized() {
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    lamp.setOnOffStatus( OnOff.ON );
    lamp.setBrightness( BRIGHTNESS );
    lamp.setColorTemperature( COLOR_TEMPERATURE );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger, never() ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
    verify( logger, never() ).info( LAMP_SET_BRIGHTNESS_PATTERN, lamp.getDefinition(), DEFAULT_BRIGHTNESS );
    verify( logger, never() ).info( LAMP_SET_COLOR_TEMPERATURE_PATTERN, lamp.getDefinition(), DEFAULT_COLOR_TEMPERATURE );
    verify( logger, never() ).info( LAMP_SWITCH_PATTERN, lamp.getDefinition(), DEFAULT_ON_OFF_STATE );
  }

  @Test
  public void ensureStatusIntegrityButUpdateTimestampIsNotExpired() {
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChanged() {
    stubOnOffItemWithStatusOn();
    lamp.setBrightness( BRIGHTNESS );
    stubBrightnessItemWithStatus( ZERO );
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, times( 2 ) ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChangedButUpdateTimestampIsNotExpired() {
    stubOnOffItemWithStatusOn();
    lamp.setBrightness( BRIGHTNESS );
    stubBrightnessItemWithStatus( ZERO );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChangedAfterInitialization() {
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChangedAfterInitializationButUpdateTimestampIsNotExpired() {
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfColorTemperatureItemChanged() {
    stubOnOffItemWithStatusOn();
    lamp.setColorTemperature( COLOR_TEMPERATURE );
    stubColorTemperatureItemWithStatus( ZERO );
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, times( 2 ) ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfColorTemperatureItemChangedButUpdateTimestampHasNotExpired() {
    stubOnOffItemWithStatusOn();
    lamp.setColorTemperature( COLOR_TEMPERATURE );
    stubColorTemperatureItemWithStatus( ZERO );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger, never() ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfColorTemperatureItemChangedAfterInitialization() {
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfOnOffItemChanged() {
    lamp.setOnOffStatus( OnOff.ON );
    stubOnOffItemWithStatusOff();
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem, times( 2 ) ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfOnOffItemChangedAfterInitialization() {
    stubOnOffItemWithStatusOff();
    lamp.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = lamp.createSyncStatus();

    lamp.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( LAMP_OUT_OF_SYNC_PATTERN, lamp.getDefinition(), expectedSyncState );
  }

  @Test
  public void toStringImplementation() {
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );
    stubOnOffItemWithStatusOn();

    String actual = lamp.toString();

    assertThat( actual )
      .isEqualTo( format( LAMP_TO_STRING_PATTERN,
                          lamp.getDefinition(),
                          Optional.of( COLOR_TEMPERATURE_OF_ITEM ),
                          Optional.of( BRIGHTNESS_OF_ITEM ),
                          Optional.of( OnOffType.ON ) ) );
  }

  @Test
  public void createSyncState() {
    lamp.setOnOffStatus( OnOff.ON );
    lamp.setColorTemperature( COLOR_TEMPERATURE );
    lamp.setBrightness( BRIGHTNESS );
    stubColorTemperatureItemWithStatus( HUNDRED );
    stubBrightnessItemWithStatus( ZERO );
    stubOnOffItemWithStatusOff();

    String actual = lamp.createSyncStatus();

    assertThat( actual )
      .isEqualTo( format( LAMP_TO_STRING_PATTERN,
                          "",
                          Optional.of( HUNDRED ) + "/" + Optional.of( COLOR_TEMPERATURE_OF_ITEM ),
                          Optional.of( ZERO ) + "/" + Optional.of( BRIGHTNESS_OF_ITEM ),
                          Optional.of( OnOffType.OFF) + "/" + Optional.of( OnOffType.ON ) ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDefinitionArgument() {
    new LampImpl( null, onOffItem, brightnessItem, colorTemperatureItem, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsOnOffItemArgument() {
    new LampImpl( BathRoomCeiling, null, brightnessItem, colorTemperatureItem, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsBrightnessItemArgument() {
    new LampImpl( BathRoomCeiling, onOffItem, null, colorTemperatureItem, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsColorTemperatureItemArgument() {
    new LampImpl( BathRoomCeiling, onOffItem, brightnessItem, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new LampImpl( BathRoomCeiling, onOffItem, brightnessItem, colorTemperatureItem, null );
  }

  private void stubOnOffItemWithStatusOn() {
    when( onOffItem.getStatus() ).thenReturn( Optional.of( OnOffType.ON ) );
  }

  private void stubOnOffItemWithStatusOff() {
    when( onOffItem.getStatus() ).thenReturn( Optional.of( OnOffType.OFF ) );
  }

  private void stubBrightnessItemWithStatus( PercentType brightness ) {
    when( brightnessItem.getStatus() ).thenReturn( Optional.of( brightness ) );
  }

  private void stubColorTemperatureItemWithStatus( PercentType colorTemperature ) {
    when( colorTemperatureItem.getStatus() ).thenReturn( Optional.of( colorTemperature ) );
  }

  private static LocalDateTime getExpiredUpdateTimestamp() {
    return LocalDateTime.now().minusSeconds( OFFSET_AFTER_LAST_UPDATE + 1 );
  }
}