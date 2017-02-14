package com.codeaffine.home.control.internal.entity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.util.Disposable;

public class EntityRelationProviderImpl implements EntityRelationProvider, Disposable {

  private final Map<EntityDefinition<?>, Collection<EntityDefinition<?>>> entityRelations;

  public EntityRelationProviderImpl() {
    entityRelations = new HashMap<>();
  }

  @Override
  public void dispose() {
    entityRelations.clear();
  }

  public void establishRelations( EntityRelationConfiguration configuration ) {
    configuration.configureFacility( parent -> children -> this.entityRelations.put( parent, asList( children ) ) );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E extends Entity<D>, D extends EntityDefinition<E>, P extends EntityDefinition<?>>
    Collection<D> getChildren( P parentDefinition, Class<D> childDefinitionType )
  {
    if( entityRelations.containsKey( parentDefinition ) ) {
      return doGetChildren( parentDefinition, childDefinitionType );
    }
    return ( Collection<D> )emptySet();
  }

  @SuppressWarnings("unchecked")
  private <E extends Entity<D>, D extends EntityDefinition<E>, P extends EntityDefinition<?>>
    Collection<D> doGetChildren( P parentDefinition, Class<D> childDefinitionType )
  {
    return ( Collection<D> )entityRelations
      .get( parentDefinition )
      .stream()
      .filter( definition -> childDefinitionType.isInstance( definition ) )
      .collect( toSet() );
  }
}