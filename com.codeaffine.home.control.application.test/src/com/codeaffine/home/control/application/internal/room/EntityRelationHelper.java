package com.codeaffine.home.control.application.internal.room;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.application.RoomProvider.RoomDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class EntityRelationHelper {

  public static void stubEntityRelation(
    EntityRelationProvider entityRelationProvider, RoomDefinition parentDefinition, BulbDefinition childDefinition )
  {
    when( entityRelationProvider.getChildren( parentDefinition, BulbDefinition.class ) )
    .thenReturn( asList( childDefinition ) );
  }
}