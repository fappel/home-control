package com.codeaffine.home.control.test.util.entity;

import static org.mockito.Mockito.*;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class EntityHelper {

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Entity<EntityDefinition<?>> stubEntity( EntityDefinition entityDefinition ) {
    Entity result = mock( Entity.class );
    when( result.getDefinition() ).thenReturn( entityDefinition );
    String toString = entityDefinition.toString();
    when( result.toString() ).thenReturn( toString );
    return result;
  }

  @SuppressWarnings("rawtypes")
  public static EntityDefinition stubEntityDefinition( String name ) {
    EntityDefinition result = mock( EntityDefinition.class );
    when( result.toString() ).thenReturn( name );
    return result;
  }
}