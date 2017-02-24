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
  public void setOnOffStatus( OnOffType newOnOffStatus ) {
    verifyNotNull( newOnOffStatus, "newOnOffStatus" );

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
        brightnessItem.updateStatus( PercentType.ZERO );
        brightnessBuffer = Optional.of( PercentType.ZERO );
      }
      setUpdateTimestamp( now() );
      logger.info( BULB_SWITCH_PATTERN, getDefinition(), newOnOffStatus );
    }
  }

  @Override
  public Optional<OnOffType> getOnOffStatus() {
    return onOffItem.getStatus();
  }

  @Override
  public void setBrightness( PercentType newBrightness ) {
    verifyNotNull( newBrightness, "newBrightness" );

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

  @Override
  public Optional<PercentType> getBrightness() {
    if( onOffItem.getStatus().equals( Optional.of( ON ) ) ) {
      return brightnessItem.getStatus();
    }
    return brightnessStatus;
  }

  @Override
  public void setColorTemperature( PercentType newColorTemperature ) {
    verifyNotNull( newColorTemperature, "newColorTemperature" );

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

  @Override
  public Optional<PercentType> getColorTemperature() {
    if( onOffItem.getStatus().equals( Optional.of( ON ) ) ) {
      return colorTemperatureItem.getStatus();
    }
    return colorTemperatureStatus;
  }

  @Override
  public String toString() {
    return format( BULB_TO_STRING_PATTERN,
                   getDefinition(),
                   colorTemperatureItem.getStatus(),
                   brightnessItem.getStatus(),
                   onOffItem.getStatus() );
  }

  public void ensure() {
    if(    updateTimestamp.plusSeconds( OFFSET_AFTER_LAST_UPDATE ).isBefore( now() )
        && (    !colorTemperatureBuffer.equals( colorTemperatureItem.getStatus() )
             || !brightnessBuffer.equals( brightnessItem.getStatus() )
             || !onOffBuffer.equals( onOffItem.getStatus() ) ) )
    {
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
}