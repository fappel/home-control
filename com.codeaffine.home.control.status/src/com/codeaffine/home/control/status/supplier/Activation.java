package com.codeaffine.home.control.status.supplier;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class Activation {

  private final Set<Zone> zones;

  public interface Zone {
    Entity<?> getZoneEntity();
    Optional<LocalDateTime> getReleaseTime();
    boolean isAdjacentActivated();
    Set<Zone> getZonesOfRelatedPaths();
  }

  public Activation( Set<Zone> zones ) {
    verifyNotNull( zones, "zones" );

    this.zones = defensiveCopy( zones );
  }

  public Set<Zone> getAllZones() {
    return defensiveCopy( zones );
  }

  public Optional<Zone> getZone( EntityDefinition<?> zoneDefinition ) {
    verifyNotNull( zoneDefinition, "zoneDefinition" );

    return zones.stream().filter( zone -> zone.getZoneEntity().getDefinition() == zoneDefinition ).findAny();
  }

  public boolean isZoneActivated( EntityDefinition<?> zoneDefinition ) {
    verifyNotNull( zoneDefinition, "zoneDefinition" );

    return getZone( zoneDefinition ).isPresent();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + zones.hashCode();
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    Activation other = ( Activation )obj;
    if( !zones.equals( other.zones ) )
      return false;
    return true;
  }

  private static Set<Zone> defensiveCopy( Set<Zone> zones ) {
    return new HashSet<>( zones );
  }
}