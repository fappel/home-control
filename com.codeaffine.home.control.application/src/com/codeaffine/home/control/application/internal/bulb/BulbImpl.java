package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.internal.bulb.Messages.*;
import static com.codeaffine.home.control.application.internal.type.TypeConverter.*;
import static com.codeaffine.home.control.type.OnOffType.ON;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Optional;

import com.codeaffine.home.control.application.bulb.BulbProvider;
import com.codeaffine.home.control.application.bulb.BulbProvider.Bulb;
import com.codeaffine.home.control.application.bulb.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbImpl implements Bulb {

  static final Percent DEFAULT_COLOR_TEMPERATURE = Percent.P_000;
  static final PercentType DEFAULT_COLOR_TEMPERATURE_INTERNAL = convertFromPercent( DEFAULT_COLOR_TEMPERATURE );
  static final Percent DEFAULT_BRIGHTNESS = Percent.P_100;
  static final PercentType DEFAULT_BRIGHTNESS_INTERNAL = convertFromPercent( DEFAULT_BRIGHTNESS );
  static final OnOff DEFAULT_ON_OFF_STATE = OnOff.OFF;
  static final OnOffType DEFAULT_ON_OFF_STATE_INTERNAL = convertFromOnOff( DEFAULT_ON_OFF_STATE );
  static final long OFFSET_AFTER_LAST_UPDATE = BulbProvider.BULB_INTEGRITY_CHECK_INTERVAL * 4;

  private final DimmerItem colorTemperatureItem;
  private final BulbDefinition definition;
  private final DimmerItem brightnessItem;
  private final SwitchItem onOffItem;
  private final Logger logger;

  private Optional<PercentType> colorTemperatureBuffer;
  private Optional<PercentType> colorTemperatureStatus;
  private Optional<PercentType> brightnessBuffer;
  private Optional<PercentType> brightnessStatus;
  private Optional<OnOffType> onOffBuffer;
  private LocalDateTime updateTimestamp;

  BulbImpl( BulbDefinition definition,
            SwitchItem onOffItem,
            DimmerItem brightnessItem,
            DimmerItem colorTemperatureItem,
            Logger logger )
  {
    verifyNotNull( colorTemperatureItem, "colorTemperatureItem" );
    verifyNotNull( definition, "definitionItem" );
    verifyNotNull( brightnessItem, "brightnessItem" );
    verifyNotNull( onOffItem, "onOffItem" );
    verifyNotNull( logger, "logger" );

    this.colorTemperatureItem = colorTemperatureItem;
    this.brightnessItem = brightnessItem;
    this.definition = definition;
    this.onOffItem = onOffItem;
    this.logger = logger;
    this.colorTemperatureBuffer = colorTemperatureItem.getStatus();
    this.colorTemperatureStatus = colorTemperatureItem.getStatus();
    this.brightnessBuffer = brightnessItem.getStatus();
    this.brightnessStatus = brightnessItem.getStatus();
    this.onOffBuffer = onOffItem.getStatus();
    setUpdateTimestamp( now() );
  }

  @Override
  public BulbDefinition getDefinition() {
    return definition;
  }

  @Override
  public void setOnOffStatus( OnOff newOnOffStatus ) {
    verifyNotNull( newOnOffStatus, "newOnOffStatus" );

    setOnOffStatusInternal( newOnOffStatus );
  }

  @Override
  public OnOff getOnOffStatus() {
    return convertToOnOff( onOffItem.getStatus(), DEFAULT_ON_OFF_STATE_INTERNAL );
  }

  @Override
  public void setBrightness( Percent newBrightness ) {
    verifyNotNull( newBrightness, "newBrightness" );

    setBrightnessInternal( newBrightness );
  }

  @Override
  public Percent getBrightness() {
    if( onOffItem.getStatus().equals( Optional.of( ON ) ) ) {
      return convertToPercent( brightnessItem.getStatus(), DEFAULT_BRIGHTNESS_INTERNAL );
    }
    return convertToPercent( brightnessStatus, DEFAULT_BRIGHTNESS_INTERNAL );
  }

  @Override
  public void setColorTemperature( Percent newColorTemperature ) {
    verifyNotNull( newColorTemperature, "newColorTemperature" );

    setColorTemperatureInternal( newColorTemperature );
  }

  @Override
  public Percent getColorTemperature() {
    if( onOffItem.getStatus().equals( Optional.of( ON ) ) ) {
      return convertToPercent( colorTemperatureItem.getStatus(), DEFAULT_COLOR_TEMPERATURE_INTERNAL );
    }
    return convertToPercent( colorTemperatureStatus, DEFAULT_COLOR_TEMPERATURE_INTERNAL );
  }

  @Override
  public String toString() {
    return format( BULB_TO_STRING_PATTERN,
                   getDefinition(),
                   colorTemperatureItem.getStatus(),
                   brightnessItem.getStatus(),
                   onOffItem.getStatus() );
  }

  public void ensureStatusIntegrity() {
    if( updateTimestamp.plusSeconds( OFFSET_AFTER_LAST_UPDATE ).isBefore( now() ) ) {
      if( isOutOfSync() ) {
        synchronize();
      } else {
        ensureDefaults();
      }
    }
  }

  public void setUpdateTimestamp( LocalDateTime updateTimestamp ) {
    this.updateTimestamp = updateTimestamp;
  }

  String createSyncStatus() {
    return format( BULB_TO_STRING_PATTERN,
                   "",
                   colorTemperatureItem.getStatus() + "/" + colorTemperatureBuffer,
                   brightnessItem.getStatus() + "/" + brightnessBuffer,
                   onOffItem.getStatus() + "/" + onOffBuffer );
  }

  private void setOnOffStatusInternal( OnOff newOnOffStatus ) {
    OnOffType value = convertFromOnOff( newOnOffStatus );
    if( !onOffItem.getStatus().equals( Optional.of( value ) ) ) {
      onOffBuffer = Optional.of( value );
      onOffItem.updateStatus( value );
      if( value == ON && colorTemperatureStatus.isPresent() ) {
        colorTemperatureItem.updateStatus( colorTemperatureStatus.get() );
        colorTemperatureBuffer = colorTemperatureStatus;
      }
      if( value == ON && brightnessStatus.isPresent() ) {
        brightnessItem.updateStatus( brightnessStatus.get() );
        brightnessBuffer = brightnessStatus;
      } else if( brightnessStatus.isPresent() ) {
        brightnessItem.updateStatus( DEFAULT_COLOR_TEMPERATURE_INTERNAL );
        brightnessBuffer = Optional.of( DEFAULT_COLOR_TEMPERATURE_INTERNAL );
      }
      setUpdateTimestamp( now() );
      logger.info( BULB_SWITCH_PATTERN, getDefinition(), newOnOffStatus );
    }
  }

  private void setBrightnessInternal( Percent newBrightness ) {
    PercentType value = convertFromPercent( newBrightness );
    brightnessStatus = Optional.of( value );
    if(    onOffItem.getStatus().equals( Optional.of( ON ) )
        && !brightnessItem.getStatus().equals( Optional.of( value ) ) )
    {
      brightnessItem.updateStatus( value );
      brightnessBuffer = Optional.of( value );
      setUpdateTimestamp( now() );
      logger.info( BULB_SET_BRIGHTNESS_PATTERN, getDefinition(), newBrightness );
    }
  }

  private void setColorTemperatureInternal( Percent newColorTemperature ) {
    PercentType value = convertFromPercent( newColorTemperature );
    colorTemperatureStatus = Optional.of( value );
    if( onOffItem.getStatus().equals( Optional.of( ON ) )
        && !colorTemperatureItem.getStatus().equals( Optional.of( value ) ) )
    {
      colorTemperatureItem.updateStatus( value );
      colorTemperatureBuffer = Optional.of( value );
      setUpdateTimestamp( now() );
      logger.info( BULB_SET_COLOR_TEMPERATURE_PATTERN, getDefinition(), newColorTemperature );
    }
  }

  private boolean isOutOfSync() {
    return    !colorTemperatureBuffer.equals( colorTemperatureItem.getStatus() )
           || !brightnessBuffer.equals( brightnessItem.getStatus() )
           || !onOffBuffer.equals( onOffItem.getStatus() );
  }

  private void synchronize() {
    logger.info( BULB_OUT_OF_SYNC_PATTERN, getDefinition(), createSyncStatus() );
    if( brightnessBuffer.isPresent() ) {
      brightnessItem.updateStatus( brightnessBuffer.get() );
    }
    if( colorTemperatureBuffer.isPresent() ) {
      colorTemperatureItem.updateStatus( colorTemperatureBuffer.get() );
    }
    onOffBuffer = Optional.of( ON );
    onOffItem.updateStatus( ON );
  }

  private void ensureDefaults() {
    if( !colorTemperatureItem.getStatus().isPresent() ) {
      colorTemperatureItem.updateStatus( DEFAULT_COLOR_TEMPERATURE_INTERNAL );
      colorTemperatureBuffer = Optional.of( DEFAULT_COLOR_TEMPERATURE_INTERNAL );
      colorTemperatureStatus = Optional.of( DEFAULT_COLOR_TEMPERATURE_INTERNAL );
      logger.info( BULB_SET_COLOR_TEMPERATURE_PATTERN, getDefinition(), DEFAULT_COLOR_TEMPERATURE );
    }
    if( !brightnessItem.getStatus().isPresent() ) {
      brightnessItem.updateStatus( DEFAULT_BRIGHTNESS_INTERNAL );
      brightnessBuffer = Optional.of( DEFAULT_BRIGHTNESS_INTERNAL );
      brightnessStatus = Optional.of( DEFAULT_BRIGHTNESS_INTERNAL );
      logger.info( BULB_SET_BRIGHTNESS_PATTERN, getDefinition(), DEFAULT_BRIGHTNESS );
    }
    if( !onOffItem.getStatus().isPresent() ) {
      onOffItem.updateStatus( DEFAULT_ON_OFF_STATE_INTERNAL );
      onOffBuffer = Optional.of( DEFAULT_ON_OFF_STATE_INTERNAL );
      logger.info( BULB_SWITCH_PATTERN, getDefinition(), DEFAULT_ON_OFF_STATE );
    }
  }
}