package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Collection;
import java.util.Optional;

import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class AllocationEventImpl implements AllocationEvent {

  private final Collection<Entity<?>> actual;
  private final Optional<Entity<?>> removed;
  private final Optional<Entity<?>> added;

  public AllocationEventImpl( Collection<Entity<?>> actual, Entity<?> added, Entity<?> removed ) {
    verifyNotNull( actual, "actual" );

    this.removed = Optional.ofNullable( removed );
    this.added = Optional.ofNullable( added );
    this.actual = actual;
  }

  @Override
  public Collection<Entity<?>> getActual() {
    return actual;
  }

  @Override
  public Optional<Entity<?>> getRemoved() {
    return removed;
  }

  @Override
  public Optional<Entity<?>> getAdded() {
    return added;
  }
}