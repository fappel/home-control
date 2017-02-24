package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.internal.bulb.BulbImpl.OFFSET_AFTER_LAST_UPDATE;
import static com.codeaffine.home.control.application.internal.bulb.BulbItemHelper.stubItem;
import static com.codeaffine.home.control.application.internal.bulb.Messages.*;
import static com.codeaffine.home.control.type.OnOffType.*;
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

import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbImplTest {

  private static final PercentType BRIGHTNESS = new PercentType( 20 );
  private static final PercentType COLOR_TEMPERATURE = new PercentType( 10 );

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
    assertThat( bulb.getOnOffStatus() ).isNotPresent();
    assertThat( bulb.getBrightness() ).isNotPresent();
    assertThat( bulb.getColorTemperature() ).isNotPresent();
    assertThat( bulb.getDefinition() ).isSameAs( BathRoomCeiling );
  }

  @Test
  public void setOnOffStatusToOn() {
    bulb.setBrightness( BRIGHTNESS );
    bulb.setColorTemperature( COLOR_TEMPERATURE );

    bulb.setOnOffStatus( ON );

    InOrder order = inOrder( onOffItem, brightnessItem, colorTemperatureItem, logger );
    order.verify( onOffItem ).updateStatus( ON );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), ON );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void setOnOffStatusToOnIfBrightnessIsNotPresent() {
    bulb.setColorTemperature( COLOR_TEMPERATURE );

    bulb.setOnOffStatus( ON );

    InOrder order = inOrder( onOffItem, colorTemperatureItem, logger );
    order.verify( onOffItem ).updateStatus( ON );
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), ON );
    order.verifyNoMoreInteractions();
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void setOnOffStatusToOnIfColorTemperatureIsNotPresent() {
    bulb.setBrightness( BRIGHTNESS );

    bulb.setOnOffStatus( ON );

    InOrder order = inOrder( onOffItem, brightnessItem, logger );
    order.verify( onOffItem ).updateStatus( ON );
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), ON );
    order.verifyNoMoreInteractions();
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void setOnOffStatusToOnIfAlreadySwitchedOn() {
    bulb.setBrightness( BRIGHTNESS );
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    stubOnOffItemWithStatusOn();

    bulb.setOnOffStatus( ON );

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( eq( BULB_SWITCH_PATTERN ), eq( bulb.getDefinition() ), any( OnOffType.class ) );
  }

  @Test
  public void setOnOffStatusToOff() {
    bulb.setBrightness( BRIGHTNESS );
    bulb.setColorTemperature( COLOR_TEMPERATURE );

    bulb.setOnOffStatus( OFF );

    InOrder order = inOrder( onOffItem, brightnessItem, logger );
    order.verify( onOffItem ).updateStatus( OFF );
    order.verify( brightnessItem ).updateStatus( ZERO );
    order.verify( logger ).info( BULB_SWITCH_PATTERN, bulb.getDefinition(), OFF );
    order.verifyNoMoreInteractions();
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
  }

  @Test
  public void getOnOffStatus() {
    stubOnOffItemWithStatusOn();

    Optional<OnOffType> actual = bulb.getOnOffStatus();

    assertThat( actual ).hasValue( ON );
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
    order.verify( brightnessItem ).updateStatus( BRIGHTNESS );
    order.verify( logger ).info( BULB_SET_BRIGHTNESS_PATTERN, bulb.getDefinition(), BRIGHTNESS );
  }

  @Test
  public void setBrightnessIfValueIsAlreadySet() {
    stubOnOffItemWithStatusOn();
    stubBrightnessItemWithStatus( BRIGHTNESS );

    bulb.setBrightness( BRIGHTNESS );

    verify( brightnessItem, never() ).updateStatus( BRIGHTNESS );
    verify( logger, never() ).info( BULB_SET_BRIGHTNESS_PATTERN, bulb.getDefinition(), BRIGHTNESS );
  }

  @Test
  public void getBrightnessIfBulbIsSwitchedOn() {
    stubOnOffItemWithStatusOn();
    stubBrightnessItemWithStatus( BRIGHTNESS );

    Optional<PercentType> actual = bulb.getBrightness();

    assertThat( actual ).hasValue( BRIGHTNESS );
  }

  @Test
  public void getBrightnessIfBulbIsNotSwitchedOn() {
    bulb.setBrightness( BRIGHTNESS );
    Optional<PercentType> actual = bulb.getBrightness();

    assertThat( actual ).hasValue( BRIGHTNESS );
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
    order.verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE );
    order.verify( logger ).info( BULB_SET_COLOR_TEMPERATURE_PATTERN, bulb.getDefinition(), COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfValueIsAlreadySet() {
    stubOnOffItemWithStatusOn();
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE );

    bulb.setColorTemperature( COLOR_TEMPERATURE );

    verify( colorTemperatureItem, never() ).updateStatus( COLOR_TEMPERATURE );
    verify( logger, never() ).info( BULB_SET_COLOR_TEMPERATURE_PATTERN, bulb.getDefinition(), COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfBulbIsSwitchedOn() {
    stubOnOffItemWithStatusOn();
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE );

    Optional<PercentType> actual = bulb.getColorTemperature();

    assertThat( actual ).hasValue( COLOR_TEMPERATURE );
  }

  @Test
  public void setColorTemperatureIfBulbIsNotSwitchedOn() {
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    Optional<PercentType> actual = bulb.getColorTemperature();

    assertThat( actual ).hasValue( COLOR_TEMPERATURE );
  }

  @Test(expected = IllegalArgumentException.class)
  public void setColorTemperatureWithNullAsArgument() {
    bulb.setColorTemperature( null );
  }

  @Test
  public void ensure() {
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfBrightnessItemChanged() {
    stubOnOffItemWithStatusOn();
    bulb.setBrightness( BRIGHTNESS );
    stubBrightnessItemWithStatus( ZERO );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem ).updateStatus( ON );
    verify( brightnessItem, times( 2 ) ).updateStatus( BRIGHTNESS );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfBrightnessItemChangedButUpdateTimestampIsNotExpired() {
    stubOnOffItemWithStatusOn();
    bulb.setBrightness( BRIGHTNESS );
    stubBrightnessItemWithStatus( ZERO );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem ).updateStatus( BRIGHTNESS );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfBrightnessItemChangedAfterInitialization() {
    stubBrightnessItemWithStatus( BRIGHTNESS );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem ).updateStatus( ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfBrightnessItemChangedAfterInitializationButUpdateTimestampIsNotExpired() {
    stubBrightnessItemWithStatus( BRIGHTNESS );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfColorTemperatureItemChanged() {
    stubOnOffItemWithStatusOn();
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    stubColorTemperatureItemWithStatus( ZERO );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem ).updateStatus( ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, times( 2 ) ).updateStatus( COLOR_TEMPERATURE );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfColorTemperatureItemChangedButUpdateTimestampHasNotExpired() {
    stubOnOffItemWithStatusOn();
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    stubColorTemperatureItemWithStatus( ZERO );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem, never() ).updateStatus( any( OnOffType.class ) );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem ).updateStatus( COLOR_TEMPERATURE );
    verify( logger, never() ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfColorTemperatureItemChangedAfterInitialization() {
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE );
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem ).updateStatus( ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfOnOffItemChanged() {
    bulb.setOnOffStatus( ON );
    stubOnOffItemWithStatusOff();
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem, times( 2 ) ).updateStatus( ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void ensureIfOnOffItemChangedAfterInitialization() {
    stubOnOffItemWithStatusOff();
    bulb.setUpdateTimestamp( getExpiredUpdateTimestamp() );
    String expectedSyncState = bulb.createSyncStatus();

    bulb.ensure();

    verify( onOffItem ).updateStatus( ON );
    verify( brightnessItem, never() ).updateStatus( any( PercentType.class ) );
    verify( colorTemperatureItem, never() ).updateStatus( any( PercentType.class ) );
    verify( logger ).info( BULB_OUT_OF_SYNC_PATTERN, bulb.getDefinition(), expectedSyncState );
  }

  @Test
  public void toStringImplementation() {
    stubColorTemperatureItemWithStatus( COLOR_TEMPERATURE );
    stubBrightnessItemWithStatus( BRIGHTNESS );
    stubOnOffItemWithStatusOn();

    String actual = bulb.toString();

    assertThat( actual )
      .isEqualTo( format( BULB_TO_STRING_PATTERN,
                          bulb.getDefinition(),
                          Optional.of( COLOR_TEMPERATURE ),
                          Optional.of( BRIGHTNESS ),
                          Optional.of( ON ) ) );
  }

  @Test
  public void createSyncState() {
    bulb.setOnOffStatus( ON );
    bulb.setColorTemperature( COLOR_TEMPERATURE );
    bulb.setBrightness( BRIGHTNESS );
    stubColorTemperatureItemWithStatus( HUNDRED );
    stubBrightnessItemWithStatus( ZERO );
    stubOnOffItemWithStatusOff();

    String actual = bulb.createSyncStatus();

    assertThat( actual )
      .isEqualTo( format( BULB_TO_STRING_PATTERN,
                          "",
                          Optional.of( HUNDRED ) + "/" + Optional.of( COLOR_TEMPERATURE ),
                          Optional.of( ZERO ) + "/" + Optional.of( BRIGHTNESS ),
                          Optional.of( OFF) + "/" + Optional.of( ON ) ) );
  }

  private void stubOnOffItemWithStatusOn() {
    when( onOffItem.getStatus() ).thenReturn( Optional.of( ON ) );
  }

  private void stubOnOffItemWithStatusOff() {
    when( onOffItem.getStatus() ).thenReturn( Optional.of( OFF ) );
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