package com.codeaffine.home.control.application.room;

import static com.codeaffine.home.control.application.internal.room.EntityRelationHelper.*;
import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.BathRoom;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.room.RoomProvider.Room;
import com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomProviderTest {

  private EntityRelationProvider entityRelationProvider;
  private RoomProvider roomProvider;

  @Before
  public void setUp() {
    entityRelationProvider = mock( EntityRelationProvider.class );
    roomProvider = new RoomProvider( entityRelationProvider );
  }

  @Test
  public void findAll() {
    Collection<Room> actual = roomProvider.findAll();

    assertThat( actual ).hasSize( RoomDefinition.values().length );
  }

  @Test
  public void findByDefinition() {
    Room actual = roomProvider.findByDefinition( BathRoom );

    assertThat( actual.getDefinition() ).isSameAs( BathRoom );
  }

  @Test
  public void dispose() {
    roomProvider.dispose();

    assertThat( roomProvider.findAll() ).isEmpty();
    assertThat( roomProvider.findByDefinition( BathRoom ) ).isNull();
  }

  @Test
  public void getChildrenOnProvidedRoom() {
    Lamp expected = mock( Lamp.class );
    stubEntityRelation( entityRelationProvider, BathRoom, BathRoomCeiling );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, BathRoomCeiling, expected );

    Room room = roomProvider.findByDefinition( RoomDefinition.BathRoom );
    Collection<Lamp> actual = room.getChildren( LampDefinition.class );

    assertThat( actual ).contains( expected );
  }

  @Test
  public void getStreamOfDefinitions() {
    Stream<RoomDefinition> actual = roomProvider.getStreamOfDefinitions();

    assertThat( actual.collect( toSet() ) )
      .hasSize( RoomDefinition.values().length )
      .contains( RoomDefinition.values() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRelationProviderArgument() {
    new RoomProvider( null );
  }
}