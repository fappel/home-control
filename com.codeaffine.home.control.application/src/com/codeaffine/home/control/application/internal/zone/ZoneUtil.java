package com.codeaffine.home.control.application.internal.zone;

import java.util.Set;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

class ZoneUtil {

  private final PathAdjacency adjacency;

  ZoneUtil( PathAdjacency adjacency ) {
    this.adjacency = adjacency;
  }

  ZoneImpl newZone( Entity<?> zoneEntity, Sensor ... sensors ) {
    return new ZoneImpl( zoneEntity, adjacency, sensors );
  }

  void markAsReleased( Zone toRelease, Path path ) {
    Set<Sensor> sensors = ( ( ZoneImpl )toRelease ).getActivationSensors();
    ZoneImpl zone = newZone( toRelease.getZoneEntity(), sensors.toArray( new Sensor[ sensors.size() ] ) );
    path.addOrReplace( zone.markAsReleased() );
  }

  static void markForInPathRelease( Entity<?> zoneEntity, Path path ) {
    path.findZoneActivation( zoneEntity ).forEach( zone -> markForInPathRelease( zone, path ) );
  }

  private static void markForInPathRelease( Zone zone, Path path ) {
    path.addOrReplace( ( ( ZoneImpl )zone ).markForInPathRelease() );
  }
}