package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Collection;

import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class AllocationEventImpl implements AllocationEvent {

  private final Collection<Entity<?>> allocations;
  private final Collection<Entity<?>> additions;
  private final Collection<Entity<?>> removals;
  private final Entity<?> actor;

  public AllocationEventImpl( Entity<?> actor,
                              Collection<Entity<?>> allocations,
                              Collection<Entity<?>> additions,
                              Collection<Entity<?>> removals )
  {
    verifyNotNull( allocations, "allocations" );
    verifyNotNull( additions, "additions" );
    verifyNotNull( removals, "removals" );
    verifyNotNull( actor, "actor" );

    this.allocations = allocations;
    this.removals = removals;
    this.additions = additions;
    this.actor = actor;
  }

  @Override
  public Entity<?> getActor() {
    return actor;
  }

  @Override
  public Collection<Entity<?>> getAllocations() {
    return allocations;
  }

  @Override
  public Collection<Entity<?>> getRemovals() {
    return removals;
  }

  @Override
  public Collection<Entity<?>> getAdditions() {
    return additions;
  }
}