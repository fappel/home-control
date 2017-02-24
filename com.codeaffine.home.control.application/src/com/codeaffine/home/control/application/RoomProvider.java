package com.codeaffine.home.control.application;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.stream.Stream;

import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.application.internal.room.RoomFactory;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomProvider extends BaseEntityProvider<Room, RoomDefinition> {

  public enum RoomDefinition implements EntityDefinition<Room> {
    Hall, Kitchen, BathRoom, BedRoom, LivingRoom
  }

  public interface Room extends CompositeEntity<RoomDefinition> {}

  public RoomProvider( EntityRelationProvider entityRelationProvider ) {
    super( new RoomFactory( verifyNotNull( entityRelationProvider, "entityRelationProvider" ) ) );
  }

  @Override
  protected Stream<RoomDefinition> getStreamOfDefinitions() {
    return Stream.of( RoomDefinition.values() );
  }
}