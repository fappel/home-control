package com.codeaffine.home.control.internal.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;

import com.codeaffine.home.control.entity.AllocationProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.util.Disposable;

public class AllocationProviderImpl implements AllocationProvider, Disposable {

  private final Collection<Entity<?>> allocations;
  private final EventBus eventBus;

  public AllocationProviderImpl( EventBus eventBus ) {
    this.eventBus = eventBus;
    this.allocations = new HashSet<>();
  }

  @Override
  public void dispose() {
    allocations.clear();
  }

  @Override
  public void allocate( Entity<?> entity ) {
    if( allocations.add( entity ) ) {
      eventBus.post( new AllocationEventImpl( defensiveCopy( allocations ), entity, null ) );
    }
  }

  @Override
  public void deallocate( Entity<?> entity ) {
    if( allocations.remove( entity ) ) {
      eventBus.post( new AllocationEventImpl( defensiveCopy( allocations ), null, entity ) );
    }
  }

  @Override
  public Collection<Entity<?>> getAllocations() {
    return defensiveCopy( allocations );
  }

  private static Collection<Entity<?>> defensiveCopy( Collection<Entity<?>> base ) {
    return new CopyOnWriteArraySet<>( base );
  }
}