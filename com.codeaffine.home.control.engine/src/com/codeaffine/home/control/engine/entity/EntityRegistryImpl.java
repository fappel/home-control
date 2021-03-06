package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
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
  }

  @Override
  public <T extends EntityProvider<?, ?>> void register( Class<T> providerType ) {
    verifyNotNull( providerType, "providerType" );

    providers.add( context.create( providerType ) );
  }

  @Override
  public Collection<Entity<?>> findAll() {
    return providers
      .stream()
      .flatMap( provider -> provider.findAll().stream() )
      .collect( toSet() );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E extends Entity<D>, D extends EntityDefinition<E>>
    Collection<E> findByDefinitionType( Class<D> definitionType )
  {
    verifyNotNull( definitionType, "definitionType" );

    return ( Collection<E> )findAll()
      .stream()
      .filter( entity -> definitionType == entity.getDefinition().getClass()  )
      .collect( toSet() );
  }
  @Override
  @SuppressWarnings("unchecked")
  public <E extends Entity<D>, D extends EntityDefinition<E>> E findByDefinition( D definition ) {
    verifyNotNull( definition, "definition" );

    return providers
      .stream()
      .filter( provider -> ( ( EntityProvider<E, D> )provider ).findByDefinition( definition ) != null )
      .map( provider -> ( ( EntityProvider<E, D> )provider ).findByDefinition( definition ) )
      .findFirst()
      .get();
  }
}