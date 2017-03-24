package com.codeaffine.home.control.application.internal.activation;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.Sensor;

public class ZoneImpl implements Zone {

  private final LocalDateTime inPathReleaseMarkTime;
  private final Set<Sensor> activationSensors;
  private final LocalDateTime releaseTime;
  private final PathAdjacency adjacency;
  private final Entity<?> zoneEntity;

  public ZoneImpl( Entity<?> zoneEntity, PathAdjacency adjacency, Sensor ... activationSensors ) {
    this( zoneEntity, adjacency, null, null, activationSensors );
  }

  private ZoneImpl( Entity<?> zoneEntity,
                    PathAdjacency adjacency,
                    LocalDateTime inPathReleaseMarkTime,
                    LocalDateTime releaseTime,
                    Sensor ... activationSensors ) {
    verifyNotNull( activationSensors, "activationSensors" );
    verifyNotNull( zoneEntity, "zoneEntity" );
    verifyNotNull( adjacency, "adjacency" );

    this.activationSensors = new HashSet<>( asList( activationSensors ) );
    this.inPathReleaseMarkTime = inPathReleaseMarkTime;
    this.releaseTime = releaseTime;
    this.zoneEntity = zoneEntity;
    this.adjacency = adjacency;
  }

  @Override
  public Entity<?> getZoneEntity() {
    return zoneEntity;
  }

  @Override
  public Set<Zone> getZonesOfRelatedPaths() {
    return adjacency.getZonesOfRelatedPaths( this );
  }

  @Override
  public boolean isAdjacentActivated() {
    return adjacency.isAdjacentActivated( zoneEntity );
  }

  @Override
  public Optional<LocalDateTime> getReleaseTime() {
    return Optional.ofNullable( releaseTime );
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( releaseTime == null ) ? 0 : releaseTime.hashCode() );
    result = prime * result + zoneEntity.hashCode();
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
    ZoneImpl other = ( ZoneImpl )obj;
    if( releaseTime == null ) {
      if( other.releaseTime != null )
        return false;
    } else if( !releaseTime.equals( other.releaseTime ) )
      return false;
    if( !zoneEntity.equals( other.zoneEntity ) )
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ZoneActivation [zone=" + zoneEntity + "]";
  }

  public ZoneImpl markAsReleased() {
    return new ZoneImpl( zoneEntity, adjacency, inPathReleaseMarkTime, now(), asArray( activationSensors ) );
  }

  public Set<Sensor> getActivationSensors() {
    return new HashSet<>( activationSensors );
  }

  public ZoneImpl addActivationSensor( Sensor sensor ) {
    Set<Sensor> sensors = getActivationSensors();
    sensors.add( sensor );
    return new ZoneImpl( zoneEntity, adjacency, asArray( sensors ) );
  }

  public ZoneImpl removeActivationSensor( Sensor sensor ) {
    Set<Sensor> sensors = getActivationSensors();
    sensors.remove( sensor );
    return new ZoneImpl( zoneEntity, adjacency, asArray( sensors ) );
  }

  public ZoneImpl markForInPathRelease() {
    return new ZoneImpl( zoneEntity, adjacency, now(), releaseTime, asArray( activationSensors ) );
  }

  public Optional<LocalDateTime> getInPathReleaseMarkTime() {
    return Optional.ofNullable( inPathReleaseMarkTime );
  }

  private static Sensor[] asArray( Set<Sensor> sensors ) {
    return sensors.stream().toArray( Sensor[]::new );
  }
}