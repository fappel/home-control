package com.codeaffine.home.control.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class EntityRelationResolver implements CompositeEntity {

  private final EntityRelationProvider relationProvider;
  private final EntityDefinition<?> definition;

  public EntityRelationResolver( EntityDefinition<?> definition, EntityRelationProvider relationProvider ) {
    this.relationProvider = relationProvider;
    this.definition = definition;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E extends Entity<D>, D extends EntityDefinition<E>> Collection<Entity<?>> getChildren() {
    return getChildren( EntityDefinition.class );
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>> Collection<E> getChildren( Class<D> childType ) {
    verifyNotNull( childType, "childType" );

    return relationProvider.getChildren( definition, childType )
        .stream()
        .map( child -> relationProvider.findByDefinition( child ) )
        .collect( toSet() );
  }
}