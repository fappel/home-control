package com.codeaffine.home.control.application.internal.room;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class EntityRelationHelper {

  public static void stubEntityRelation(
    EntityRelationProvider entityRelationProvider, RoomDefinition parentDefinition, LampDefinition childDefinition )
  {
    when( entityRelationProvider.getChildren( parentDefinition, LampDefinition.class ) )
      .thenReturn( asList( childDefinition ) );
  }

  public static void stubEntityRelationForAllChildren(
    EntityRelationProvider entityRelationProvider, EntityDefinition<?> parent, EntityDefinition<?> child )
  {
    when( entityRelationProvider.getChildren( parent, EntityDefinition.class ) ).thenReturn( asList( child ) );

  }

  public static void stubRegistryWithEntityInstanceForDefinition(
    EntityRelationProvider entityRelationProvider, LampDefinition entityDefinition, Lamp entity )
  {
    when( entityRelationProvider.findByDefinition( entityDefinition ) ).thenReturn( entity );
  }
}