package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;

import com.codeaffine.home.control.application.bulb.BulbProvider.Bulb;
import com.codeaffine.home.control.application.bulb.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.Subscribe;

class ControlCenter {

  private final EntityRegistry entityRegistry;

  ControlCenter( EntityRegistry entityRegistry ) {
    this.entityRegistry = entityRegistry;
  }

  @Subscribe
  void onEvent( Event event ) {
    event.getSource( ZoneActivation.class ).ifPresent( zoneActivation -> {
      Set<Bulb> on = collectBulbsToSwitchOn( zoneActivation );
      Collection<Bulb> bulbs = entityRegistry.findByDefinitionType( BulbDefinition.class );
      Set<Bulb> off = bulbs.stream().filter( bulb -> !on.contains( bulb ) ).collect( toSet() );
      on.forEach( bulb -> { bulb.setBrightness( P_100 ); bulb.setOnOffStatus( ON ); } );
      off.forEach( bulb -> bulb.setOnOffStatus( OFF ) );
    } );
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