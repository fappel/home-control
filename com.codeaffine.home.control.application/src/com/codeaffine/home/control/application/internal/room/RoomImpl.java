package com.codeaffine.home.control.application.internal.room;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomImpl implements Room {

  private final EntityRelationProvider relationProvider;
  private final RoomDefinition definition;

  RoomImpl( RoomDefinition definition, EntityRelationProvider relationProvider ) {
    this.relationProvider = relationProvider;
    this.definition = definition;
  }

  @Override
  public RoomDefinition getDefinition() {
    return definition;
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>> Collection<E> getChildren( Class<D> childType ) {
    return relationProvider.getChildren( definition, childType )
      .stream()
      .map( child -> relationProvider.findByDefinition( child ) )
      .collect( toSet() );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E extends Entity<D>, D extends EntityDefinition<E>> Collection<Entity<?>> getChildren() {
    return relationProvider.getChildren( definition )
      .stream()
      .map( child -> relationProvider.findByDefinition( ( D )child ) )
      .collect( toSet() );
  }
}