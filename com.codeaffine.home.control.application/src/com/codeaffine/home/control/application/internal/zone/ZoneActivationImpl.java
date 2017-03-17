package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Optional;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

class ZoneActivationImpl implements ZoneActivation {

  private final PathAdjacency adjacency;
  private final Entity<?> zone;

  private LocalDateTime inPathReleaseMarkTime;
  private LocalDateTime releaseTime;

  ZoneActivationImpl( Entity<?> zone, PathAdjacency adjacency ) {
    verifyNotNull( adjacency, "adjacency" );
    verifyNotNull( zone, "zone" );

    this.adjacency = adjacency;
    this.zone = zone;
  }

  @Override
  public Entity<?> getZone() {
    return zone;
  }

  @Override
  public boolean isAdjacentActivated() {
    return adjacency.isAdjacentActivated( zone );
  }

  @Override
  public Optional<LocalDateTime> getReleaseTime() {
    return Optional.ofNullable( releaseTime );
  }

  public void markAsReleased() {
    releaseTime = now();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( releaseTime == null ) ? 0 : releaseTime.hashCode() );
    result = prime * result + zone.hashCode();
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
    ZoneActivationImpl other = ( ZoneActivationImpl )obj;
    if( releaseTime == null ) {
      if( other.releaseTime != null )
        return false;
    } else if( !releaseTime.equals( other.releaseTime ) )
      return false;
    if( !zone.equals( other.zone ) )
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ZoneActivation [zone=" + zone + "]";
  }

  void markForInPathRelease() {
    inPathReleaseMarkTime = now();
  }

  Optional<LocalDateTime> getInPathReleaseMarkTime() {
    return Optional.ofNullable( inPathReleaseMarkTime );
  }
}