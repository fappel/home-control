package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static java.lang.Math.*;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;

import com.codeaffine.home.control.application.bulb.BulbProvider.Bulb;
import com.codeaffine.home.control.application.bulb.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.application.internal.activity.ActivityImpl;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.Subscribe;

class ControlCenter {

  private final EntityRegistry entityRegistry;

  private Percent brightnessMinimum;
  private Percent colorTemperature;
  private Percent brightness;

  ControlCenter( EntityRegistry entityRegistry ) {
    this.entityRegistry = entityRegistry;
    this.brightnessMinimum = P_001;
    this.colorTemperature = P_000;
    this.brightness = P_100;
  }

  @Subscribe
  void onEvent( Event event ) {
    event.getSource( ZoneActivation.class ).ifPresent( zoneActivation -> {
      Set<Bulb> on = collectBulbsToSwitchOn( zoneActivation );
      Collection<Bulb> bulbs = entityRegistry.findByDefinitionType( BulbDefinition.class );
      Set<Bulb> off = bulbs.stream().filter( bulb -> !on.contains( bulb ) ).collect( toSet() );
      on.forEach( bulb -> bulb.setOnOffStatus( ON ) );
      off.forEach( bulb -> bulb.setOnOffStatus( OFF ) );
    } );

    event.getSource( ActivityImpl.class ).ifPresent( activity -> {
      brightnessMinimum = P_001;
      if( activity.getActivityRate().compareTo( P_050 ) > 1 ) {
        brightnessMinimum = P_020;
      }
      updateBulbs();
    } );

    event.getSource( SunPositionProvider.class ).ifPresent( sunPositionProvider -> {
      double zenitAngle = sunPositionProvider.getSunPosition().getZenit();

      // color temperature
      double temperatureFactor = max( 2.0, ( abs( ( 10 + now().getDayOfYear() ) % 366 - 183 ) / 31 ) );
      double value = min( ( ( zenitAngle + 7 ) * temperatureFactor ), 100.0 );
      colorTemperature = Percent.valueOf( ( int )( 100.0 - max( 0.0, value ) ) );

      double brightnessFactor = min( 3.33, max( 2.0, ( abs( ( 10 + now().getDayOfYear() ) % 366 - 183 ) / 51.85 ) ) );
      brightness = Percent.valueOf( ( int )max( brightnessMinimum.intValue(), min( ( ( zenitAngle + 18 ) * brightnessFactor ), 99.0 ) ) );
      updateBulbs();
    } );
  }

  private void updateBulbs() {
    Collection<Bulb> bulbs = entityRegistry.findByDefinitionType( BulbDefinition.class );
    bulbs.forEach( bulb -> bulb.setColorTemperature( colorTemperature ) );
    bulbs.forEach( bulb -> bulb.setBrightness( brightness ) );
  }

  private static Set<Bulb> collectBulbsToSwitchOn( ZoneActivation zoneActivation ) {
    return zoneActivation
      .getActiveZones()
      .stream()
      .flatMap( zone -> getZoneBulbs( zone ).stream() )
      .collect( toSet() );
  }

  private static Collection<Bulb> getZoneBulbs( Entity<EntityDefinition<?>> zone ) {
    return ( ( CompositeEntity<?> )zone ).getChildren( BulbDefinition.class );
  }
}