package com.codeaffine.home.control.application.internal.room;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.BathRoomCeiling;
import static com.codeaffine.home.control.application.RoomProvider.RoomDefinition.BathRoom;
import static com.codeaffine.home.control.application.internal.room.EntityRelationHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomImplTest {

  private EntityRelationProvider entityRelationProvider;
  private RoomImpl room;

  @Before
  public void setUp() {
    entityRelationProvider = mock( EntityRelationProvider.class );
    room = new RoomImpl( BathRoom, entityRelationProvider );
  }

  @Test
  public void getDefinition() {
    RoomDefinition actual = room.getDefinition();

    assertThat( actual ).isSameAs( BathRoom );
  }

  @Test
  public void getChildrenWithChildTypeParameter() {
    Bulb expected = mock( Bulb.class );
    stubEntityRelation( entityRelationProvider, BathRoom, BathRoomCeiling );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, BathRoomCeiling, expected );

    Collection<Bulb> actual = room.getChildren( BulbDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( expected );
  }

  @Test
  public void getChildren() {
    Bulb expected = mock( Bulb.class );
    stubEntityRelationForAllChildren( entityRelationProvider, BathRoom, BathRoomCeiling );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, BathRoomCeiling, expected );

    Collection<Entity<?>> actual = room.getChildren();

    assertThat( actual )
      .hasSize( 1 )
      .contains( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildrenWithChildTypeParameterAndNullAsArgument() {
    room.getChildren( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDefinitionArgument() {
    new RoomImpl( null, entityRelationProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRelationProviderArgument() {
    new RoomImpl( BathRoom, null );
  }

  @Test
  public void toStringImplementation() {
    String actual = room.toString();

    assertThat( actual.toString() ).contains( room.getDefinition().toString() );
  }
}