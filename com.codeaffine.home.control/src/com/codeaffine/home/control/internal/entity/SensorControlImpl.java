package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.home.control.internal.entity.Sets.asSet;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;

class SensorControlImpl implements SensorControl {

  private final Set<Entity<EntityDefinition<?>>> zones;
  private final Entity<EntityDefinition<?>> sensor;
  private final ZoneProviderImpl zoneProvider;

  private boolean active;

  public SensorControlImpl( Entity<?> sensor, ZoneProviderImpl zoneProvider ) {
    this.zoneProvider = zoneProvider;
    this.sensor = cast( sensor );
    this.zones = new HashSet<>();
    this.active = false;
  }

  @Override
  public void registerZone( Entity<?> zone ) {
    verifyNotNull( zone, "zone" );

    if( active && !zones.contains( zone ) ) {
      zoneProvider.engage( sensor, asSet( cast( zone ) ) );
    }
    zones.add( cast( zone ) );
  }

  @Override
  public void unregisterZone( Entity<?> zone ) {
    verifyNotNull( zone, "zone" );

    if( active && zones.contains( zone ) ) {
      zoneProvider.release( sensor, asSet( cast( zone ) ) );
    }
    zones.remove( zone );
  }

  @Override
  public void engage() {
    zoneProvider.engage( sensor, copyZones() );
    active = true;
  }

  @Override
  public void release() {
    zoneProvider.release( sensor, copyZones() );
    active = false;
  }

  private Set<Entity<EntityDefinition<?>>> copyZones() {
    return new HashSet<>( zones );
  }

  @SuppressWarnings("unchecked")
  private static Entity<EntityDefinition<?>> cast( Entity<?> entity ) {
    return ( Entity<EntityDefinition<?>> )entity;
  }
}