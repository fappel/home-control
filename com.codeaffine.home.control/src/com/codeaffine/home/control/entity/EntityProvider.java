package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface EntityProvider<E extends Entity<D>, D extends EntityDefinition<E>> {

  interface EntityDefinition<E> {}

  interface Entity<D> {
    D getDefinition();
  }

  interface EntityFactory<E, D> {
    E create( D definition );
  }

  interface EntityRegistry {
    <E extends Entity<D>, D extends EntityDefinition<E>> E findByDefinition( D child );
    <T extends EntityProvider<?,?>> void register( Class<T> providerType );

  }

  interface EntityProviderConfiguration {
    void registerEntities( EntityRegistry entityRegistry );
  }

  Collection<? extends Entity<D>> findAll();
  E findByDefinition( D definition );
}