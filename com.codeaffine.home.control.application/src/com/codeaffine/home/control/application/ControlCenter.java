package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.type.OnOffType.*;
import static com.codeaffine.home.control.type.PercentType.HUNDRED;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;

import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.Subscribe;

class ControlCenter {

  private final ZoneActivation zoneActivation;
  private final EntityRegistry entityRegistry;

  ControlCenter( ZoneActivation zoneActivation, EntityRegistry entityRegistry ) {
    this.zoneActivation = zoneActivation;
    this.entityRegistry = entityRegistry;
  }

  @Subscribe
  void onEvent( Event event ) {
    Set<Bulb> on = zoneActivation.getActiveZones().stream().flatMap( zone -> getZoneBulbs( zone ).stream() ).collect( toSet() );
    Collection<Bulb> bulbs = entityRegistry.findByDefinitionType( BulbDefinition.class );
    Set<Bulb> off = bulbs.stream().filter( bulb -> !on.contains( bulb ) ).collect( toSet() );
    on.forEach( bulb -> { bulb.setBrightness( HUNDRED ); bulb.setOnOffStatus( ON ); } );
    off.forEach( bulb -> bulb.setOnOffStatus( OFF ) );
  }

  private static Collection<Bulb> getZoneBulbs( Entity<EntityDefinition<?>> zone ) {
    return ( ( CompositeEntity<?> )zone ).getChildren( BulbDefinition.class );
  }
}