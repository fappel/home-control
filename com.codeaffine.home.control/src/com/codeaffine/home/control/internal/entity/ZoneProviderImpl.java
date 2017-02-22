package com.codeaffine.home.control.internal.entity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneEvent;
import com.codeaffine.home.control.entity.ZoneProvider;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.util.Disposable;

public class ZoneProviderImpl implements ZoneProvider, Disposable {

  private final Map<Entity<EntityDefinition<?>>, Set<Entity<EntityDefinition<?>>>> engagedZones;
  private final EventBus eventBus;

  public ZoneProviderImpl( EventBus eventBus ) {
    this.eventBus = eventBus;
    this.engagedZones = new HashMap<>();
  }

  @Override
  public void dispose() {
    engagedZones.clear();
  }

  public void engage( Entity<EntityDefinition<?>> sensor, Set<Entity<EntityDefinition<?>>> zones ) {
    Set<Entity<EntityDefinition<?>>> additions = new HashSet<>();
    zones.forEach( zone -> updateOnEngage( sensor, zone, additions ) );
    if( !additions.isEmpty() ) {
      eventBus.post( new ZoneEvent( sensor, getEngagedZones(), additions, emptySet() ) );
    }
  }

  public void release( Entity<EntityDefinition<?>> sensor, Set<Entity<EntityDefinition<?>>> zones ) {
    Set<Entity<EntityDefinition<?>>> removals = new HashSet<>();
    zones.forEach( zone -> updateOnDeallocate( sensor, zone, removals ) );
    if( !removals.isEmpty() ) {
      eventBus.post( new ZoneEvent( sensor, getEngagedZones(), emptySet(), removals ) );
    }
  }

  @Override
  public Set<Entity<EntityDefinition<?>>> getEngagedZones() {
    return defensiveCopy( engagedZones.keySet() );
  }

  private static Set<Entity<EntityDefinition<?>>> defensiveCopy( Set<Entity<EntityDefinition<?>>> base ) {
    return new HashSet<>( base );
  }

  private void updateOnEngage( Entity<EntityDefinition<?>> sensor,
                               Entity<EntityDefinition<?>> zone,
                               Set<Entity<EntityDefinition<?>>> additions )
  {
    if( !engagedZones.containsKey( zone ) ) {
      engagedZones.put( zone, new HashSet<>( asList( sensor ) ) );
      additions.add( zone );
    } else {
      engagedZones.get( zone ).add( sensor );
    }
  }

  private void updateOnDeallocate( Entity<EntityDefinition<?>> sensor,
                                   Entity<EntityDefinition<?>> zone,
                                   Set<Entity<EntityDefinition<?>>> removals )
  {
    if( engagedZones.containsKey( zone ) ) {
      Set<Entity<EntityDefinition<?>>> sensors = engagedZones.get( zone );
      sensors.remove( sensor );
      if( sensors.isEmpty() ) {
        engagedZones.remove( zone );
        removals.add( zone );
      }
    }
  }
}