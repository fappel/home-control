package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;

import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class ZoneEvent {

  private final Set<Entity<EntityDefinition<?>>> engagedZones;
  private final Set<Entity<EntityDefinition<?>>> additions;
  private final Set<Entity<EntityDefinition<?>>> removals;
  private final Entity<EntityDefinition<?>> sensor;

  public ZoneEvent( Entity<EntityDefinition<?>> sensor,
                    Set<Entity<EntityDefinition<?>>> engagedZones,
                    Set<Entity<EntityDefinition<?>>> additions,
                    Set<Entity<EntityDefinition<?>>> removals )
  {
    verifyNotNull( engagedZones, "engagedZones" );
    verifyNotNull( additions, "additions" );
    verifyNotNull( removals, "removals" );
    verifyNotNull( sensor, "sensor" );

    this.engagedZones = engagedZones;
    this.additions = additions;
    this.removals = removals;
    this.sensor = sensor;
  }

  public Entity<EntityDefinition<?>> getSensor() {
    return sensor;
  }

  public Set<Entity<EntityDefinition<?>>> getEngagedZones() {
    return engagedZones;
  }

  public Set<Entity<EntityDefinition<?>>> getRemovals() {
    return removals;
  }

  public Set<Entity<EntityDefinition<?>>> getAdditions() {
    return additions;
  }
}