package com.codeaffine.home.control.application.internal.room;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomFactory implements EntityFactory<Room, RoomDefinition> {

  private final EntityRelationProvider entityRelationProvider;

  public RoomFactory( EntityRelationProvider entityRelationProvider ) {
    verifyNotNull( entityRelationProvider, "entityRelationProvider" );

    this.entityRelationProvider = entityRelationProvider;
  }

  @Override
  public Room create( RoomDefinition definition ) {
    verifyNotNull( definition, "definition" );

    return new RoomImpl( definition, entityRelationProvider );
  }
}