package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface EntityRelationProvider {

  public interface EntityRelationConfiguration {
    void configureFacility( Facility facility );
  }

  public interface Facility {
    Relation equip( EntityDefinition<?> parent );
  }

  public interface Relation {
    void with( EntityDefinition<?> ... children );
  }

  <E extends Entity<C>, C extends EntityDefinition<E>, P extends EntityDefinition<?>>
    Collection<C> getChildren( P parentDefinition, Class<C> childDefinitionType );
  <E extends Entity<D>, D extends EntityDefinition<E>> Collection<E> findByDefinitionType( Class<D> definitionType );
  <E extends Entity<D>, D extends EntityDefinition<E>> E findByDefinition( D definition );
  Collection<Entity<?>> findAll();
}