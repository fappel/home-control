package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.status.type.Percent.*;
import static com.codeaffine.home.control.type.OnOffType.ON;
import static java.lang.Math.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusSupplier;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.HeartBeatSupplier;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.status.type.Percent;


public class AdjustBrightnessOperation implements HomeControlOperation {

  private static final int BRIGHTNESS_DEFAULT = 50;

  private final EntityRegistry entityRegistry;
  private final SwitchItem autoBrightness;
  private final NumberItem brightnessItem;

  private Percent brightnessMiniumumBelowThreshold;
  private Percent brightnessMinimumAboveThreshold;
  private Percent brightnessMinimum;
  private Percent activityThreshold;
  private Percent brightness;

  public AdjustBrightnessOperation( EntityRegistry entityRegistry,
                                    @ByName( "autoBrightness" ) SwitchItem autoBrightnessSwitch,
                                    @ByName( "brightness" ) NumberItem brightnessItem )
  {
    this.entityRegistry = entityRegistry;
    this.autoBrightness = autoBrightnessSwitch;
    this.brightnessItem = brightnessItem;
    this.brightnessMinimum = P_001;
    this.brightness = P_100;
    reset();
  }

  public void setActivityThreshold( Percent activityThreshold ) {
    this.activityThreshold = activityThreshold;
  }

  public void setBrightnessMiniumumBelowThreshold( Percent brightnessMiniumumBelowThreshold ) {
    this.brightnessMiniumumBelowThreshold = brightnessMiniumumBelowThreshold;
  }

  public void setBrightnessMinimumAboveThreshold( Percent brightnessMinimumAboveThreshold ) {
    this.brightnessMinimumAboveThreshold = brightnessMinimumAboveThreshold;
  }

  @Override
  public Collection<Class<? extends StatusSupplier<?>>> getRelatedStatusSupplierTypes() {
    return asList( ActivitySupplier.class, SunPositionSupplier.class, HeartBeatSupplier.class );
  }

  @Override
  public void reset() {
    activityThreshold = P_050;
    brightnessMinimumAboveThreshold = P_020;
    brightnessMiniumumBelowThreshold = P_001;
  }

  @Override
  public void executeOn( StatusEvent event ) {
    event.getSource( ActivitySupplier.class ).ifPresent( activity -> {
      brightnessMinimum = brightnessMiniumumBelowThreshold;
      if( activity.getStatus().getOverallActivity().compareTo( activityThreshold ) > 1 ) {
        brightnessMinimum = brightnessMinimumAboveThreshold;
      }
    } );

    event.getSource( SunPositionSupplier.class ).ifPresent( sunPositionSupplier -> {
      if( isAutoBrightnessOn()) {
        brightness = calculateBrightness( sunPositionSupplier );
        adjustBrightnessOfLamps( brightness );
      } else {
        adjustBrightnessOfLamps( getBrightnessSetting() );
      }
    } );

    event.getSource( HeartBeatSupplier.class ).ifPresent( heartBeat -> {
      adjustBrightnessOfLamps( isAutoBrightnessOn() ? brightness : getBrightnessSetting() );
    });
  }

  private Percent getBrightnessSetting() {
    return Percent.valueOf( brightnessItem.getStatus( BRIGHTNESS_DEFAULT ) );
  }

  private boolean isAutoBrightnessOn() {
    return autoBrightness.getStatus( ON ) == ON;
  }

  private Percent calculateBrightness( SunPositionSupplier sunPositionSupplier ) {
    double zenitAngle = sunPositionSupplier.getStatus().getZenit();
    double brightnessFactor = min( 3.33, max( 2.0, ( abs( ( 10 + now().getDayOfYear() ) % 366 - 183 ) / 51.85 ) ) );
    return Percent.valueOf( ( int )max( brightnessMinimum.intValue(),
                                        min( ( ( zenitAngle + 18 ) * brightnessFactor ), 99.0 ) ) );
  }

  private void adjustBrightnessOfLamps( Percent brightness ) {
    Collection<Lamp> lamps = entityRegistry.findByDefinitionType( LampDefinition.class );
    lamps.forEach( lamp -> lamp.setBrightness( brightness ) );
  }
}