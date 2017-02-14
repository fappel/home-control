package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.RoomProvider.RoomDefinition.BathRoom;
import static com.codeaffine.home.control.application.internal.room.EntityRegistryHelper.stubRegistryWithEntityInstanceForDefinition;
import static com.codeaffine.home.control.application.internal.room.EntityRelationHelper.stubEntityRelation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.application.RoomProvider.Room;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomProviderTest {

  private EntityRelationProvider entityRelationProvider;
  private EntityRegistry entityRegistry;
  private RoomProvider roomProvider;

  @Before
  public void setUp() {
    entityRegistry = mock( EntityRegistry.class );
    entityRelationProvider = mock( EntityRelationProvider.class );
    roomProvider = new RoomProvider( entityRelationProvider, entityRegistry );
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
    Bulb expected = mock( Bulb.class );
    stubEntityRelation( entityRelationProvider, BathRoom, BathRoomCeiling );
    stubRegistryWithEntityInstanceForDefinition( entityRegistry, BathRoomCeiling, expected );

    Room room = roomProvider.findByDefinition( RoomDefinition.BathRoom );
    Collection<Bulb> actual = room.getChildren( BulbDefinition.class );

    assertThat( actual ).contains( expected );
  }
}