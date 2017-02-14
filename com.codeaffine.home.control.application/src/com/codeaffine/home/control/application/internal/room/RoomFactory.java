package com.codeaffine.home.control.application.internal.room;

import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomFactory implements EntityFactory<Room, RoomDefinition> {

  private final EntityRelationProvider childEntityProvider;
  private final EntityRegistry entityRegistry;

  public RoomFactory( EntityRelationProvider childEntityProvider, EntityRegistry entityRegistry ) {
    this.childEntityProvider = childEntityProvider;
    this.entityRegistry = entityRegistry;
  }

  @Override
  public Room create( RoomDefinition definition ) {
    return new RoomImpl( definition, childEntityProvider, entityRegistry );
  }
}