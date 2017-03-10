package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Optional;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

class ZoneActivationImpl implements ZoneActivation {

  private final Entity<?> zone;

  private LocalDateTime releaseTime;

  ZoneActivationImpl( Entity<?> zone ) {
    verifyNotNull( zone, "zone" );

    this.zone = zone;
  }

  @Override
  public Entity<?> getZone() {
    return zone;
  }

  @Override
  public Optional<LocalDateTime> getReleaseTime() {
    return Optional.ofNullable( releaseTime );
  }

  public void markRelease() {
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
}