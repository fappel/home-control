package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface EntityProvider<E extends Entity<D>, D extends EntityDefinition<E>> {

  interface EntityDefinition<E> {}

  interface Entity<D> {
    D getDefinition();
  }

  interface CompositeEntity<D> extends Entity<D> {
    <R extends Entity<C>, C extends EntityDefinition<R>> Collection<R> getChildren( Class<C> childType );
    <R extends Entity<C>, C extends EntityDefinition<R>> Collection<Entity<?>> getChildren();
  }

  interface EntityFactory<E, D> {
    E create( D definition );
  }

  interface EntityRegistry {
    <E extends Entity<D>, D extends EntityDefinition<E>> E findByDefinition( D child );
    <T extends EntityProvider<?,?>> void register( Class<T> providerType );
    Collection<Entity<?>> findAll();
  }

  interface EntityProviderConfiguration {
    void registerEntities( EntityRegistry entityRegistry );
  }

  Collection<? extends Entity<D>> findAll();
  E findByDefinition( D definition );
}