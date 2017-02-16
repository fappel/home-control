package com.codeaffine.home.control.application;

import java.util.stream.Stream;

import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.application.internal.room.RoomFactory;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.CompositeEntity;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomProvider extends BaseEntityProvider<Room, RoomDefinition> {

  public enum RoomDefinition implements EntityDefinition<Room> {
    Hall, Kitchen, BathRoom, BedRoom, LivingRoom
  }

  public interface Room extends Entity<RoomDefinition>, CompositeEntity {}

  public RoomProvider( EntityRelationProvider entityRelationProvider ) {
    super( new RoomFactory( entityRelationProvider ) );
  }

  @Override
  protected Stream<RoomDefinition> getStreamOfDefinitions() {
    return Stream.of( RoomDefinition.values() );
  }
}