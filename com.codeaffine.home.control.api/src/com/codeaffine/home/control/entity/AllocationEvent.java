package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;

import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class AllocationEvent {

  private final Set<Entity<EntityDefinition<?>>> allocated;
  private final Set<Entity<EntityDefinition<?>>> additions;
  private final Set<Entity<EntityDefinition<?>>> removals;
  private final Entity<EntityDefinition<?>> sensor;

  public AllocationEvent( Entity<EntityDefinition<?>> sensor,
                          Set<Entity<EntityDefinition<?>>> allocated,
                          Set<Entity<EntityDefinition<?>>> additions,
                          Set<Entity<EntityDefinition<?>>> removals )
  {
    verifyNotNull( allocated, "allocated" );
    verifyNotNull( additions, "additions" );
    verifyNotNull( removals, "removals" );
    verifyNotNull( sensor, "sensor" );

    this.allocated = allocated;
    this.additions = additions;
    this.removals = removals;
    this.sensor = sensor;
  }

  public Entity<EntityDefinition<?>> getSensor() {
    return sensor;
  }

  public Set<Entity<EntityDefinition<?>>> getAllocated() {
    return allocated;
  }

  public Set<Entity<EntityDefinition<?>>> getRemovals() {
    return removals;
  }

  public Set<Entity<EntityDefinition<?>>> getAdditions() {
    return additions;
  }
}