package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.type.Percent.*;
import static java.lang.Math.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.internal.activity.ActivityProviderImpl;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusProvider;


public class AdjustBrightnessOperation implements HomeControlOperation {

  private final EntityRegistry entityRegistry;

  private Percent brightnessMiniumumBelowThreshold;
  private Percent brightnessMinimumAboveThreshold;
  private Percent brightnessMinimum;
  private Percent activityThreshold;
  private Percent brightness;

  public AdjustBrightnessOperation( EntityRegistry entityRegistry ) {
    this.entityRegistry = entityRegistry;
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
  public Collection<Class<? extends StatusProvider<?>>> getRelatedStatusProviderTypes() {
    return asList( ActivityProviderImpl.class, SunPositionProvider.class );
  }

  @Override
  public void reset() {
    activityThreshold = P_050;
    brightnessMinimumAboveThreshold = P_020;
    brightnessMiniumumBelowThreshold = P_001;
  }

  @Override
  public void executeOn( StatusEvent event ) {
    event.getSource( ActivityProviderImpl.class ).ifPresent( activity -> {
      brightnessMinimum = brightnessMiniumumBelowThreshold;
      if( activity.getStatus().getOverallActivity().compareTo( activityThreshold ) > 1 ) {
        brightnessMinimum = brightnessMinimumAboveThreshold;
      }
      adjustBrightnessOfLamps();
    } );

    event.getSource( SunPositionProvider.class ).ifPresent( sunPositionProvider -> {
      double zenitAngle = sunPositionProvider.getStatus().getZenit();
      double brightnessFactor = min( 3.33, max( 2.0, ( abs( ( 10 + now().getDayOfYear() ) % 366 - 183 ) / 51.85 ) ) );
      brightness = Percent.valueOf( ( int )max( brightnessMinimum.intValue(), min( ( ( zenitAngle + 18 ) * brightnessFactor ), 99.0 ) ) );
      adjustBrightnessOfLamps();
    } );
  }

  private void adjustBrightnessOfLamps() {
    Collection<Lamp> lamps = entityRegistry.findByDefinitionType( LampDefinition.class );
    lamps.forEach( lamp -> lamp.setBrightness( brightness ) );
  }
}