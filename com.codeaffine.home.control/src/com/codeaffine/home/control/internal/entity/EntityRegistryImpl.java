package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashSet;
import java.util.Set;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.entity.EntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class EntityRegistryImpl implements EntityRegistry {

  private final Set<EntityProvider<?, ?>> providers;
  private final Context context;

  public EntityRegistryImpl( Context context ) {
    this.providers = new HashSet<>();
    this.context = context;
    context.set( EntityRegistry.class, this );
  }

  @Override
  public <T extends EntityProvider<?, ?>> void register( Class<T> providerType ) {
    verifyNotNull( providerType, "providerType" );

    providers.add( context.create( providerType ) );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E extends Entity<D>, D extends EntityDefinition<E>> E findByDefinition( D child ) {
    return providers
      .stream()
      .filter( provider -> ( ( EntityProvider<E, D> )provider ).findByDefinition( child ) != null )
      .map( provider -> ( ( EntityProvider<E, D> )provider ).findByDefinition( child ) )
      .findFirst()
      .get();
  }
}