package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.ZoneActivationProviderImpl.PATH_EXPIRED_TIMEOUT;
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

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

class Path {

  private final List<ZoneActivation> activations;

  private Supplier<LocalDateTime> timeSupplier;

  Path() {
    this.activations = new LinkedList<>();
    this.timeSupplier = () -> now();
  }

  boolean isEmpty() {
    return activations.isEmpty();
  }

  int size() {
    return activations.size();
  }

  void addOrReplace( ZoneActivation zoneActivation ) {
    remove( findZoneActivation( zoneActivation.getZone() ) );
    activations.add( zoneActivation );
  }

  Collection<ZoneActivation> getAll() {
    return new LinkedList<>( activations );
  }

  Set<ZoneActivation> find( Predicate<? super ZoneActivation> predicate ) {
    return activations.stream().filter( predicate ).collect( toSet() );
  }

  Set<ZoneActivation> findZoneActivation( Entity<?> zone ) {
    return find( activation -> activation.getZone().equals( zone ) );
  }

  Set<ZoneActivation> findInPathReleases() {
    return find( activation -> ( ( ZoneActivationImpl )activation ).getInPathReleaseMarkTime().isPresent() );
  }

  boolean remove( Set<ZoneActivation> zoneActivations ) {
    return activations.removeAll( zoneActivations );
  }

  boolean isExpired() {
    return activations.size() == 1
        && activations.get( 0 ).getReleaseTime().isPresent()
        && activations.get( 0 ).getReleaseTime().get().plusSeconds( PATH_EXPIRED_TIMEOUT )
             .isBefore( timeSupplier.get() );
  }

  Optional<LocalDateTime> getLatestReleaseTime() {
    return find( activation -> activation.getReleaseTime().isPresent() )
      .stream()
      .reduce( ( activation1, activation2 ) -> getLatestReleasedActivation( activation1, activation2 ) )
      .map( activation -> activation.getReleaseTime().get() );
  }

  void setTimeSupplier( Supplier<LocalDateTime> timeSupplier ) {
    this.timeSupplier = timeSupplier;
  }

  private static ZoneActivation getLatestReleasedActivation( ZoneActivation activation1, ZoneActivation activation2 ) {
    if( activation1.getReleaseTime().get().isBefore( activation2.getReleaseTime().get() ) ) {
      return activation2;
    }
    return activation1;
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