package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.internal.bulb.Messages.*;
import static com.codeaffine.home.control.application.internal.type.TypeConverter.*;
import static com.codeaffine.home.control.type.OnOffType.ON;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Optional;

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

  static final PercentType DEFAULT_COLOR_TEMPERATURE = PercentType.ZERO;
  static final PercentType DEFAULT_BRIGHTNESS = PercentType.HUNDRED;
  static final OnOffType DEFAULT_ON_OFF_STATE = OnOffType.OFF;
  static final long OFFSET_AFTER_LAST_UPDATE = 4L;

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

    setOnOffStatusInternal( convertFromOnOff( newOnOffStatus ) );
  }

  @Override
  public OnOff getOnOffStatus() {
    return convertToOnOff( onOffItem.getStatus(), DEFAULT_ON_OFF_STATE );
  }

  @Override
  public void setBrightness( Percent newBrightness ) {
    verifyNotNull( newBrightness, "newBrightness" );

    setBrightnessInternal( convertFromPercent( newBrightness ) );
  }

  @Override
  public Percent getBrightness() {
    if( onOffItem.getStatus().equals( Optional.of( ON ) ) ) {
      return convertToPercent( brightnessItem.getStatus(), DEFAULT_BRIGHTNESS );
    }
    return convertToPercent( brightnessStatus, DEFAULT_BRIGHTNESS );
  }

  @Override
  public void setColorTemperature( Percent newColorTemperature ) {
    verifyNotNull( newColorTemperature, "newColorTemperature" );

    setColorTemperatureInternal( convertFromPercent( newColorTemperature ) );
  }

  @Override
  public Percent getColorTemperature() {
    if( onOffItem.getStatus().equals( Optional.of( ON ) ) ) {
      return convertToPercent( colorTemperatureItem.getStatus(), DEFAULT_COLOR_TEMPERATURE );
    }
    return convertToPercent( colorTemperatureStatus, DEFAULT_COLOR_TEMPERATURE );
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

  private void setOnOffStatusInternal( OnOffType newOnOffStatus ) {
    if( !onOffItem.getStatus().equals( Optional.of( newOnOffStatus ) ) ) {
      onOffBuffer = Optional.of( newOnOffStatus );
      onOffItem.updateStatus( newOnOffStatus );
      if( newOnOffStatus == ON && colorTemperatureStatus.isPresent() ) {
        colorTemperatureItem.updateStatus( colorTemperatureStatus.get() );
        colorTemperatureBuffer = colorTemperatureStatus;
      }
      if( newOnOffStatus == ON && brightnessStatus.isPresent() ) {
        brightnessItem.updateStatus( brightnessStatus.get() );
        brightnessBuffer = brightnessStatus;
      } else if( brightnessStatus.isPresent() ) {
        brightnessItem.updateStatus( DEFAULT_COLOR_TEMPERATURE );
        brightnessBuffer = Optional.of( DEFAULT_COLOR_TEMPERATURE );
      }
      setUpdateTimestamp( now() );
      logger.info( BULB_SWITCH_PATTERN, getDefinition(), newOnOffStatus );
    }
  }

  private void setBrightnessInternal( PercentType newBrightness ) {
    brightnessStatus = Optional.of( newBrightness );
    if(    onOffItem.getStatus().equals( Optional.of( ON ) )
        && !brightnessItem.getStatus().equals( Optional.of( newBrightness ) ) )
    {
      brightnessItem.updateStatus( newBrightness );
      brightnessBuffer = Optional.of( newBrightness );
      setUpdateTimestamp( now() );
      logger.info( BULB_SET_BRIGHTNESS_PATTERN, getDefinition(), newBrightness );
    }
  }

  private void setColorTemperatureInternal( PercentType newColorTemperature ) {
    colorTemperatureStatus = Optional.of( newColorTemperature );
    if( onOffItem.getStatus().equals( Optional.of( ON ) )
        && !colorTemperatureItem.getStatus().equals( Optional.of( newColorTemperature ) ) )
    {
      colorTemperatureItem.updateStatus( newColorTemperature );
      colorTemperatureBuffer = Optional.of( newColorTemperature );
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
      colorTemperatureItem.updateStatus( DEFAULT_COLOR_TEMPERATURE );
      colorTemperatureBuffer = Optional.of( DEFAULT_COLOR_TEMPERATURE );
      colorTemperatureStatus = Optional.of( DEFAULT_COLOR_TEMPERATURE );
      logger.info( BULB_SET_COLOR_TEMPERATURE_PATTERN, getDefinition(), DEFAULT_COLOR_TEMPERATURE );
    }
    if( !brightnessItem.getStatus().isPresent() ) {
      brightnessItem.updateStatus( DEFAULT_BRIGHTNESS );
      brightnessBuffer = Optional.of( DEFAULT_BRIGHTNESS );
      brightnessStatus = Optional.of( DEFAULT_BRIGHTNESS );
      logger.info( BULB_SET_BRIGHTNESS_PATTERN, getDefinition(), DEFAULT_BRIGHTNESS );
    }
    if( !onOffItem.getStatus().isPresent() ) {
      onOffItem.updateStatus( DEFAULT_ON_OFF_STATE );
      onOffBuffer = Optional.of( DEFAULT_ON_OFF_STATE );
      logger.info( BULB_SWITCH_PATTERN, getDefinition(), DEFAULT_ON_OFF_STATE );
    }
  }
}