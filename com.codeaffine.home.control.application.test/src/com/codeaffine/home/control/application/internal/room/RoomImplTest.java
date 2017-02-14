package com.codeaffine.home.control.application.internal.room;

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
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomImplTest {

  private EntityRelationProvider entityRelationProvider;
  private EntityRegistry entityRegistry;
  private RoomImpl room;

  @Before
  public void setUp() {
    entityRegistry = mock( EntityRegistry.class );
    entityRelationProvider = mock( EntityRelationProvider.class );
    room = new RoomImpl( BathRoom, entityRelationProvider, entityRegistry );
  }

  @Test
  public void getDefinition() {
    RoomDefinition actual = room.getDefinition();

    assertThat( actual ).isSameAs( BathRoom );
  }

  @Test
  public void getChildren() {
    Bulb expected = mock( Bulb.class );
    stubEntityRelation( entityRelationProvider, BathRoom, BathRoomCeiling );
    stubRegistryWithEntityInstanceForDefinition( entityRegistry, BathRoomCeiling, expected );

    Collection<Bulb> actual = room.getChildren( BulbDefinition.class );

    assertThat( actual ).contains( expected );
  }
}