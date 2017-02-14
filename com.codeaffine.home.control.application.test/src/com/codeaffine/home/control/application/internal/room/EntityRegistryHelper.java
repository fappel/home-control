package com.codeaffine.home.control.application.internal.room;

import static org.mockito.Mockito.when;

import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class EntityRegistryHelper {

  public static void stubRegistryWithEntityInstanceForDefinition(
    EntityRegistry entityRegistry, BulbDefinition entityDefinition, Bulb entity )
  {
    when( entityRegistry.findByDefinition( entityDefinition ) ).thenReturn( entity );
  }
}