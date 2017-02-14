package com.codeaffine.home.control.application.internal.room;

import java.util.Collection;
import java.util.stream.Collectors;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomImpl implements Room {

  private final EntityRelationProvider relationProvider;
  private final EntityRegistry entityRegistry;
  private final RoomDefinition definition;

  RoomImpl( RoomDefinition definition, EntityRelationProvider relationProvider, EntityRegistry entityRegistry ) {
    this.relationProvider = relationProvider;
    this.entityRegistry = entityRegistry;
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
      .map( child -> findChildEntity( child ) )
      .collect( Collectors.toSet() );
  }

  private <E extends Entity<D>, D extends EntityDefinition<E>> E findChildEntity( D child ) {
    return entityRegistry.findByDefinition( child );
  }
}