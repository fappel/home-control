package com.codeaffine.home.control.application.internal.room;

import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.BathRoom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.room.RoomProvider.Room;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class RoomFactoryTest {

  private RoomFactory factory;

  @Before
  public void setUp() {
    factory = new RoomFactory( mock( EntityRelationProvider.class ) );
  }

  @Test
  public void create() {
    Room actual = factory.create( BathRoom );

    assertThat( actual.getDefinition() ).isSameAs( BathRoom );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinitionArgument() {
    factory.create( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRelationProviderArgument() {
    new RoomFactory( null );
  }
}