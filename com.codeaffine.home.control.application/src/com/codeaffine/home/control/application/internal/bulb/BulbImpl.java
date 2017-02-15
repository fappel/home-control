package com.codeaffine.home.control.application.internal.bulb;

import static com.codeaffine.home.control.type.OnOffType.ON;
import static java.lang.String.format;

import java.util.Optional;

import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbImpl implements Bulb {

  static final String TO_STRING_PATTERN = "%s [colorTemperature=%s, brightness=%s, onOff=%s]";

  private final DimmerItem colorTemperature;
  private final BulbDefinition definition;
  private final DimmerItem brightness;
  private final SwitchItem onOff;

  private Optional<PercentType> colorTemperatureBuffer;
  private Optional<PercentType> brightnessBuffer;
  private Optional<OnOffType> onOffBuffer;

  BulbImpl( BulbDefinition definition, SwitchItem onOff, DimmerItem brightness, DimmerItem colorTemperature ) {
    this.definition = definition;
    this.colorTemperature = colorTemperature;
    this.brightness = brightness;
    this.onOff = onOff;
    colorTemperatureBuffer = colorTemperature.getStatus();
    brightnessBuffer = brightness.getStatus();
    onOffBuffer = onOff.getStatus();
  }

  @Override
  public BulbDefinition getDefinition() {
    return definition;
  }

  @Override
  public void setOnOffStatus( OnOffType onOffStatus ) {
    this.onOffBuffer = Optional.of( onOffStatus );
    onOff.setStatus( onOffStatus );
  }

  @Override
  public Optional<OnOffType> getOnOffStatus() {
    return onOff.getStatus();
  }

  @Override
  public void setBrightness( PercentType percent ) {
    this.brightnessBuffer = Optional.of( percent );
    brightness.setStatus( percent );
  }

  @Override
  public Optional<PercentType> getBrightness() {
    return brightness.getStatus();
  }

  @Override
  public Optional<PercentType> getColorTemperature() {
    return colorTemperature.getStatus();
  }

  @Override
  public void setColorTemperature( PercentType percent ) {
    this.colorTemperatureBuffer = Optional.of( percent );
    colorTemperature.setStatus( percent );
  }

  @Override
  public String toString() {
    return format( TO_STRING_PATTERN,
                   getDefinition(),
                   colorTemperature.getStatus(),
                   brightness.getStatus(),
                   onOff.getStatus() );
  }

  public void ensure() {
    if(    !colorTemperatureBuffer.equals( colorTemperature.getStatus() )
        || !brightnessBuffer.equals( brightness.getStatus() )
        || !onOffBuffer.equals( onOff.getStatus() ) )
    {
      if( brightnessBuffer.isPresent() ) {
        brightness.setStatus( brightnessBuffer.get() );
      }
      if( colorTemperatureBuffer.isPresent() ) {
        colorTemperature.setStatus( colorTemperatureBuffer.get() );
      }
      onOffBuffer = Optional.of( ON );
      onOff.setStatus( ON );
    }
  }
}