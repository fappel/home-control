package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.ActivationProviderImpl.PATH_EXPIRED_TIMEOUT;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toSet;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

class Path {

  private final List<Zone> activations;

  private Supplier<LocalDateTime> timeSupplier;

  Path() {
    this.activations = new LinkedList<>();
    this.timeSupplier = () -> now();
  }

  void addOrReplace( Zone zone ) {
    remove( findZoneActivation( zone.getZoneEntity() ) );
    activations.add( zone );
  }

  Collection<Zone> getAll() {
    return new LinkedList<>( activations );
  }

  Set<Zone> find( Predicate<? super Zone> predicate ) {
    return activations.stream().filter( predicate ).collect( toSet() );
  }

  Set<Zone> findZoneActivation( Entity<?> zoneEntity ) {
    return find( zone -> zone.getZoneEntity().equals( zoneEntity ) );
  }

  Set<Zone> findInPathReleases() {
    return find( zone -> ( ( ZoneImpl )zone ).getInPathReleaseMarkTime().isPresent() );
  }

  boolean remove( Set<Zone> zones ) {
    return activations.removeAll( zones );
  }

  boolean isExpired() {
    return activations.size() == 1
        && activations.get( 0 ).getReleaseTime().isPresent()
        && activations.get( 0 ).getReleaseTime().get().plusSeconds( PATH_EXPIRED_TIMEOUT )
             .isBefore( timeSupplier.get() );
  }

  Optional<LocalDateTime> getLatestReleaseTime() {
    return find( zone -> zone.getReleaseTime().isPresent() )
      .stream()
      .reduce( ( zone1, zone2 ) -> getLatestReleasedActivation( zone1, zone2 ) )
      .map( zone -> zone.getReleaseTime().get() );
  }

  void setTimeSupplier( Supplier<LocalDateTime> timeSupplier ) {
    this.timeSupplier = timeSupplier;
  }

  private static Zone getLatestReleasedActivation( Zone zone1, Zone zone2 ) {
    if( zone1.getReleaseTime().get().isBefore( zone2.getReleaseTime().get() ) ) {
      return zone2;
    }
    return zone1;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + activations.hashCode();
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
    Path other = ( Path )obj;
    if( !activations.equals( other.activations ) )
      return false;
    return true;
  }
}