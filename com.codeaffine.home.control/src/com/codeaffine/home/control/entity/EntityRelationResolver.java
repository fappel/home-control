package com.codeaffine.home.control.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class EntityRelationResolver<D extends EntityDefinition<?>> implements CompositeEntity<D> {

  private final EntityRelationProvider relationProvider;
  private final D definition;

  public EntityRelationResolver( D definition, EntityRelationProvider relationProvider ) {
    this.relationProvider = relationProvider;
    this.definition = definition;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R extends Entity<C>, C extends EntityDefinition<R>> Collection<Entity<?>> getChildren() {
    return getChildren( EntityDefinition.class );
  }

  @Override
  public <R extends Entity<C>, C extends EntityDefinition<R>> Collection<R> getChildren( Class<C> childType ) {
    verifyNotNull( childType, "childType" );

    return relationProvider.getChildren( definition, childType )
        .stream()
        .map( child -> relationProvider.findByDefinition( child ) )
        .collect( toSet() );
  }

  @Override
  public D getDefinition() {
    return definition;
  }
}