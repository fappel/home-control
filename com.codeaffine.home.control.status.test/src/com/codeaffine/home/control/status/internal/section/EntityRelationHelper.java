package com.codeaffine.home.control.status.internal.section;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.test.util.entity.MyEntity;
import com.codeaffine.home.control.test.util.entity.MyEntityDefinition;

public class EntityRelationHelper {

  public static void stubEntityRelation(
    EntityRelationProvider entityRelationProvider, EntityDefinition<?> parent, MyEntityDefinition child )
  {
    when( entityRelationProvider.getChildren( parent, MyEntityDefinition.class ) )
      .thenReturn( asList( child ) );
  }

  public static void stubEntityRelationForAllChildren(
    EntityRelationProvider entityRelationProvider, EntityDefinition<?> parent, EntityDefinition<?> child )
  {
    when( entityRelationProvider.getChildren( parent, EntityDefinition.class ) ).thenReturn( asList( child ) );

  }

  public static void stubRegistryWithEntityInstanceForDefinition(
    EntityRelationProvider entityRelationProvider, MyEntityDefinition entityDefinition, MyEntity entity )
  {
    when( entityRelationProvider.findByDefinition( entityDefinition ) ).thenReturn( entity );
  }
}