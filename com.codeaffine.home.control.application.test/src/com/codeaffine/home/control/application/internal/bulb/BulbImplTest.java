package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.bulb.BulbProvider.BulbDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.internal.bulb.BulbImpl.*;
import static com.codeaffine.home.control.application.internal.bulb.BulbItemHelper.stubItem;
import static com.codeaffine.home.control.application.internal.bulb.Messages.*;
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

import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbImplTest {

  private static final Percent BRIGHTNESS = Percent.P_020;
  private static final Percent COLOR_TEMPERATURE = Percent.P_010;
  private static final PercentType BRIGHTNESS_OF_ITEM = new PercentType( BRIGHTNESS.intValue() );
  private static final PercentType COLOR_TEMPERATURE_OF_ITEM = new PercentType( COLOR_TEMPERATURE.intValue() );

  private DimmerItem colorTemperatureItem;
  private DimmerItem brightnessItem;
  private SwitchItem onOffItem;
  private Logger logger;
  private BulbImpl bulb;

  @Before
  public void setUp() {
    onOffItem = stubItem( SwitchItem.class );
    brightnessItem = stubItem( DimmerItem.class );
    colorTemperatureItem = stubItem( DimmerItem.class );
    logger = mock( Logger.class );
    bulb = new BulbImpl( BathRoomCeiling, onOffItem, brightnessItem, colorTemperatureItem, logger );
  }

  @Test
  public void initialization() {
    assertThat( bulb.getOnOffStatus() ).isSameAs( OnOff.OFF );
    assertThat( bulb.getBrightness() ).isSameAs( Percent.P_100 );
    assertThat( bulb.getColorTemperature() ).isSameAs( Percent.P_000 );
    assertThat( bulb.getDefinition() ).isSameAs( BathRoomCeiling );
  }

  @Test
  public void setOnOffStatusToOn() {
    bulb.setBrightness( BRIGHTNESS );
    bulb.setColorTemperature( COLOR_TEMPERATURE );

    bulb.setOnOffStatus( OnOff.ON );

    InOrder order = inOrder( onOffItem, brightnessItem, colorTemperatureItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.ON );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), OnOff.ON );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void setOnOffStatusToOnIfBrightnessIsNotPresent() {
    bulb.setColorTemperature( COLOR_TEMPERATURE );

    bulb.setOnOffStatus( OnOff.ON );

    InOrder order = inOrder( onOffItem, colorTemperatureItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.ON );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), OnOff.ON );
    order.verifyNoMoreInteractions();
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void setOnOffStatusToOnIfColorTemperatureIsNotPresent() {
    bulb.setBrightness( BRIGHTNESS );

    bulb.setOnOffStatus( OnOff.ON );

    InOrder order = inOrder( onOffItem, brightnessItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.ON );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), OnOff.ON );
    order.verifyNoMoreInteractions();
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void setOnOffStatusToOnIfAlreadySwitchedOn() {
    bulb.setBrightness( BRIGHTNESS );
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    stubOnOffItemWithStatusOn();

    bulb.setOnOffStatus( OnOff.ON );

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( eq( BULB_SWITCH_PATTERN ), eq( bulb.getDefinition() ), any( OnOffType.class ) );
  }

  @Test
  public void setOnOffStatusToOff() {
    bulb.setBrightness( BRIGHTNESS );
    bulb.setColorTemperature( COLOR_TEMPERATURE );

    bulb.setOnOffStatus( OnOff.OFF );

    InOrder order = inOrder( onOffItem, brightnessItem, logger );
    order.verify( onOffItem ).updateStatus( OnOffType.OFF );
    order.verify( brightnessItem ).updateStatus( ZERO );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), OnOff.OFF );
    order.verifyNoMoreInteractions();
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void getOnOffStatus() {
    stubOnOffItemWithStatusOn();

    OnOff actual = bulb.getOnOffStatus();

    assertThat( actual ).isSameAs( OnOff.ON );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setOnOffStatusWithNullAsArgument() {
    bulb.setOnOffStatus( null );
  }

  @Test
  public void setBrightness() {
    stubOnOffItemWithStatusOn();

    bulb.setBrightness( BRIGHTNESS );

    InOrder order = inOrder( brightnessItem, logger );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    order.verify( logger ).info( BULB_SET_BRIGHTNESS_PATTERN, bulb.getDefinition(), BRIGHTNESS );
  }

  @Test
  public void setBrightnessIfValueIsAlreadySet() {
    stubOnOffItemWithStatusOn();
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );

    bulb.setBrightness( BRIGHTNESS );

    verify( brightnessItem, never() ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( logger, never() ).info( BULB_SET_BRIGHTNESS_PATTERN, bulb.getDefinition(), BRIGHTNESS );
  }

  @Test
  public void getBrightnessIfBulbIsSwitchedOn() {
    stubOnOffItemWithStatusOn();
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );

    Percent actual = bulb.getBrightness();

    assertThat( actual ).isSameAs( BRIGHTNESS );
  }

  @Test
  public void getBrightnessIfBulbIsNotSwitchedOn() {
    bulb.setBrightness( BRIGHTNESS );
    Percent actual = bulb.getBrightness();

    assertThat( actual ).isSameAs( BRIGHTNESS );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setBrightnessWithNullAsArgument() {
    bulb.setBrightness( null );
  }

  @Test
  public void setColorTemperature() {
    stubOnOffItemWithStatusOn();

    bulb.setColorTemperature( COLOR_TEMPERATURE );

    InOrder order = inOrder( colorTemperatureItem, logger );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    order.verify( logger ).info( BULB_SET_COLOR_TEMPERATURE_PATTERN, bulb.getDefinition(), COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfValueIsAlreadySet() {
    stubOnOffItemWithStatusOn();
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );

    bulb.setColorTemperature( COLOR_TEMPERATURE );

    verify( colorTemperatureItem, never() ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger, never() ).info( BULB_SET_COLOR_TEMPERATURE_PATTERN, bulb.getDefinition(), COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfBulbIsSwitchedOn() {
    stubOnOffItemWithStatusOn();
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );

    Percent actual = bulb.getColorTemperature();

    assertThat( actual ).isSameAs( COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfBulbIsNotSwitchedOn() {
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    Percent actual = bulb.getColorTemperature();

    assertThat( actual ).isSameAs( COLOR_TEMPERATURE );
  }

  @Test(expected = IllegalArgumentException.class)
  public void setColorTemperatureWithNullAsArgument() {
    bulb.setColorTemperature( null );
  }

  @Test
  public void ensureStatusIntegrity() {
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( DEFAULT_ON_OFF_STATE_INTERNAL );
    verify( brightnessItem ).updateStatus( DEFAULT_BRIGHTNESS_INTERNAL );
    verify( colorTemperatureItem ).updateStatus( DEFAULT_COLOR_TEMPERATURE_INTERNAL );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
    verify( logger ).info( BULB_SET_BRIGHTNESS_PATTERN, bulb.getDefinition(), DEFAULT_BRIGHTNESS );
    verify( logger ).info( BULB_SET_COLOR_TEMPERATURE_PATTERN, bulb.getDefinition(), DEFAULT_COLOR_TEMPERATURE );
    verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), DEFAULT_ON_OFF_STATE );
  }

  @Test
  public void ensureStatusIntegrityIfBulbIsInitialized() {
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    bulb.setOnOffStatus( OnOff.ON );
    bulb.setBrightness( BRIGHTNESS );
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
    verify( logger, never() ).info( BULB_SET_BRIGHTNESS_PATTERN, bulb.getDefinition(), DEFAULT_BRIGHTNESS );
    verify( logger, never() ).info( BULB_SET_COLOR_TEMPERATURE_PATTERN, bulb.getDefinition(), DEFAULT_COLOR_TEMPERATURE );
    verify( logger, never() ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), DEFAULT_ON_OFF_STATE );
  }

  @Test
  public void ensureStatusIntegrityButUpdateTimestampIsNotExpired() {
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChanged() {
    stubOnOffItemWithStatusOn();
    bulb.setBrightness( BRIGHTNESS );
    stubBrightnessItemWithStatus( ZERO );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, times( 2 ) ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChangedButUpdateTimestampIsNotExpired() {
    stubOnOffItemWithStatusOn();
    bulb.setBrightness( BRIGHTNESS );
    stubBrightnessItemWithStatus( ZERO );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem ).updateStatus( BRIGHTNESS_OF_ITEM );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChangedAfterInitialization() {
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfBrightnessItemChangedAfterInitializationButUpdateTimestampIsNotExpired() {
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfColorTemperatureItemChanged() {
    stubOnOffItemWithStatusOn();
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    stubColorTemperatureItemWithStatus( ZERO );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, times( 2 ) ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfColorTemperatureItemChangedButUpdateTimestampHasNotExpired() {
    stubOnOffItemWithStatusOn();
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    stubColorTemperatureItemWithStatus( ZERO );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE_OF_ITEM );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfColorTemperatureItemChangedAfterInitialization() {
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfOnOffItemChanged() {
    bulb.setOnOffStatus( OnOff.ON );
    stubOnOffItemWithStatusOff();
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem, times( 2 ) ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureStatusIntegrityIfOnOffItemChangedAfterInitialization() {
    stubOnOffItemWithStatusOff();
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensureStatusIntegrity();

    verify( onOffItem ).updateStatus( OnOffType.ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void toStringImplementation() {
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE_OF_ITEM );
    stubBrightnessItemWithStatus( BRIGHTNESS_OF_ITEM );
    stubOnOffItemWithStatusOn();

    String actual = bulb.toString();

    assertThat( actual )
      .isEqualTo( format( BULB_TO_STRING_PATTERN,
                          bulb.getDefinition(),
                          Optional.of( COLOR_TEMPERATURE_OF_ITEM ),
                          Optional.of( BRIGHTNESS_OF_ITEM ),
                          Optional.of( OnOffType.ON ) ) );
  }

  @Test
  public void createSyncState() {
    bulb.setOnOffStatus( OnOff.ON );
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    bulb.setBrightness( BRIGHTNESS );
    stubColorTemperatureItemWithStatus( HUNDRED );
    stubBrightnessItemWithStatus( ZERO );
    stubOnOffItemWithStatusOff();

    String actual = bulb.createSyncStatus();

    assertThat( actual )
      .isEqualTo( format( BULB_TO_STRING_PATTERN,
                          "",
                          Optional.of( HUNDRED ) + "/" + Optional.of( COLOR_TEMPERATURE_OF_ITEM ),
                          Optional.of( ZERO ) + "/" + Optional.of( BRIGHTNESS_OF_ITEM ),
                          Optional.of( OnOffType.OFF) + "/" + Optional.of( OnOffType.ON ) ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDefinitionArgument() {
    new BulbImpl( null, onOffItem, brightnessItem, colorTemperatureItem, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsOnOffItemArgument() {
    new BulbImpl( BathRoomCeiling, null, brightnessItem, colorTemperatureItem, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsBrightnessItemArgument() {
    new BulbImpl( BathRoomCeiling, onOffItem, null, colorTemperatureItem, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsColorTemperatureItemArgument() {
    new BulbImpl( BathRoomCeiling, onOffItem, brightnessItem, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new BulbImpl( BathRoomCeiling, onOffItem, brightnessItem, colorTemperatureItem, null );
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