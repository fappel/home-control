package com.codeaffine.home.control.application.internal.room;

import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomFactory implements EntityFactory<Room, RoomDefinition> {

  private final EntityRelationProvider childEntityProvider;

  public RoomFactory( EntityRelationProvider childEntityProvider ) {
    this.childEntityProvider = childEntityProvider;
  }

  @Override
  public Room create( RoomDefinition definition ) {
    return new RoomImpl( definition, childEntityProvider );
  }
}