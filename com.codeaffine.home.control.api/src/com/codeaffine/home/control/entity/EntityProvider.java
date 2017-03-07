package com.codeaffine.home.control.entity;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public interface EntityProvider<E extends Entity<D>, D extends EntityDefinition<E>> {

  public interface EntityDefinition<E> {}

  public interface Entity<D> {
    D getDefinition();
  }

  public interface CompositeEntity<D> extends Entity<D> {
    <R extends Entity<C>, C extends EntityDefinition<R>> Collection<R> getChildren( Class<C> childType );
    <R extends Entity<C>, C extends EntityDefinition<R>> Collection<Entity<?>> getChildren();
  }

  public interface EntityFactory<E, D> {
    E create( D definition );
  }

  public interface EntityRegistry {
    Collection<Entity<?>> findAll();
    <E extends Entity<D>, D extends EntityDefinition<E>> Collection<E> findByDefinitionType( Class<D> definitionType );
    <E extends Entity<D>, D extends EntityDefinition<E>> E findByDefinition( D definition );
    <T extends EntityProvider<?,?>> void register( Class<T> providerType );
  }

  public interface EntityProviderConfiguration {
    void configureEntities( EntityRegistry entityRegistry );
  }

  Collection<? extends Entity<D>> findAll();
  E findByDefinition( D definition );
}