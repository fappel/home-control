package com.codeaffine.home.control.application.internal.room;

import java.util.Collection;

import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.entity.EntityRelationResolver;

public class RoomImpl implements Room {

  private final EntityRelationResolver<RoomDefinition> entityRelationResolver;

  RoomImpl( RoomDefinition definition, EntityRelationProvider relationProvider ) {
    this.entityRelationResolver = new EntityRelationResolver<>( definition, relationProvider );
  }

  @Override
  public RoomDefinition getDefinition() {
    return entityRelationResolver.getDefinition();
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>> Collection<E> getChildren( Class<D> childType ) {
    return entityRelationResolver.getChildren( childType );
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>> Collection<Entity<?>> getChildren() {
    return entityRelationResolver.getChildren();
  }

  @Override
  public String toString() {
    return "Room [definition=" + getDefinition() + "]";
  }
}