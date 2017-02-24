package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.application.internal.bulb.Messages.*;
import static com.codeaffine.home.control.type.OnOffType.ON;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Optional;

import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbImpl implements Bulb {

  static final long OFFSET_AFTER_LAST_UPDATE = 4L;

  private final DimmerItem colorTemperature;
  private final BulbDefinition definition;
  private final DimmerItem brightness;
  private final SwitchItem onOff;
  private final Logger logger;

  private Optional<PercentType> colorTemperatureBuffer;
  private Optional<PercentType> colorTemperatureStatus;
  private Optional<PercentType> brightnessBuffer;
  private Optional<PercentType> brightnessStatus;
  private Optional<OnOffType> onOffBuffer;
  private LocalDateTime updateTimestamp;

  BulbImpl(
    BulbDefinition definition, SwitchItem onOff, DimmerItem brightness, DimmerItem colorTemperature, Logger logger )
  {
    this.colorTemperature = colorTemperature;
    this.definition = definition;
    this.brightness = brightness;
    this.logger = logger;
    this.onOff = onOff;
    this.colorTemperatureBuffer = colorTemperature.getStatus();
    this.colorTemperatureStatus = colorTemperature.getStatus();
    this.brightnessBuffer = brightness.getStatus();
    this.brightnessStatus = brightness.getStatus();
    this.onOffBuffer = onOff.getStatus();
    setUpdateTimestamp( now() );
  }

  @Override
  public BulbDefinition getDefinition() {
    return definition;
  }

  @Override
  public void setOnOffStatus( OnOffType newOnOffStatus ) {
    verifyNotNull( newOnOffStatus, "newOnOffStatus" );

    if( !onOff.getStatus().equals( Optional.of( newOnOffStatus ) ) ) {
      onOffBuffer = Optional.of( newOnOffStatus );
      onOff.updateStatus( newOnOffStatus );
      if( newOnOffStatus == ON && colorTemperatureStatus.isPresent() ) {
        colorTemperature.updateStatus( colorTemperatureStatus.get() );
        colorTemperatureBuffer = colorTemperatureStatus;
      }
      if( newOnOffStatus == ON && brightnessStatus.isPresent() ) {
        brightness.updateStatus( brightnessStatus.get() );
        brightnessBuffer = brightnessStatus;
      } else if( brightnessStatus.isPresent() ) {
        brightness.updateStatus( PercentType.ZERO );
        brightnessBuffer = Optional.of( PercentType.ZERO );
      }
      setUpdateTimestamp( now() );
      logger.info( BULB_SWITCH_PATTERN, getDefinition(), newOnOffStatus );
    }
  }

  @Override
  public Optional<OnOffType> getOnOffStatus() {
    return onOff.getStatus();
  }

  @Override
  public void setBrightness( PercentType newBrightness ) {
    verifyNotNull( newBrightness, "newBrightness" );

    brightnessStatus = Optional.of( newBrightness );
    if(    onOff.getStatus().equals( Optional.of( ON ) )
        && !brightness.getStatus().equals( Optional.of( newBrightness ) ) )
    {
      brightness.updateStatus( newBrightness );
      brightnessBuffer = Optional.of( newBrightness );
      setUpdateTimestamp( now() );
      logger.info( BULB_SET_BRIGHTNESS_PATTERN, getDefinition(), newBrightness );
    }
  }

  @Override
  public Optional<PercentType> getBrightness() {
    if( onOff.getStatus().equals( Optional.of( ON ) ) ) {
      return brightness.getStatus();
    }
    return brightnessStatus;
  }

  @Override
  public void setColorTemperature( PercentType newColorTemperature ) {
    verifyNotNull( newColorTemperature, "newColorTemperature" );

    colorTemperatureStatus = Optional.of( newColorTemperature );
    if( onOff.getStatus().equals( Optional.of( ON ) )
        && !colorTemperature.getStatus().equals( Optional.of( newColorTemperature ) ) )
    {
      colorTemperature.updateStatus( newColorTemperature );
      colorTemperatureBuffer = Optional.of( newColorTemperature );
      setUpdateTimestamp( now() );
      logger.info( BULB_SET_COLOR_TEMPERATURE_PATTERN, getDefinition(), newColorTemperature );
    }
  }

  @Override
  public Optional<PercentType> getColorTemperature() {
    if( onOff.getStatus().equals( Optional.of( ON ) ) ) {
      return colorTemperature.getStatus();
    }
    return colorTemperatureStatus;
  }

  @Override
  public String toString() {
    return format( BULB_TO_STRING_PATTERN,
                   getDefinition(),
                   colorTemperature.getStatus(),
                   brightness.getStatus(),
                   onOff.getStatus() );
  }

  public void ensure() {
    if(    updateTimestamp.plusSeconds( OFFSET_AFTER_LAST_UPDATE ).isBefore( now() )
        && (    !colorTemperatureBuffer.equals( colorTemperature.getStatus() )
             || !brightnessBuffer.equals( brightness.getStatus() )
             || !onOffBuffer.equals( onOff.getStatus() ) ) )
    {
      logger.info( BULB_OUT_OF_SYNC_PATTERN, getDefinition(), createSyncStatus() );
      if( brightnessBuffer.isPresent() ) {
        brightness.updateStatus( brightnessBuffer.get() );
      }
      if( colorTemperatureBuffer.isPresent() ) {
        colorTemperature.updateStatus( colorTemperatureBuffer.get() );
      }
      onOffBuffer = Optional.of( ON );
      onOff.updateStatus( ON );
    }
  }

  public void setUpdateTimestamp( LocalDateTime updateTimestamp ) {
    this.updateTimestamp = updateTimestamp;
  }

  String createSyncStatus() {
    return format( BULB_TO_STRING_PATTERN,
                   "",
                   colorTemperature.getStatus() + "/" + colorTemperatureBuffer,
                   brightness.getStatus() + "/" + brightnessBuffer,
                   onOff.getStatus() + "/" + onOffBuffer );
  }
}