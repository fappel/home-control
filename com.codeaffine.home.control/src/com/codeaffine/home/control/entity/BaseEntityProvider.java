package com.codeaffine.home.control.entity;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.util.Disposable;

public abstract class BaseEntityProvider<E extends Entity<D>, D extends EntityDefinition<E>>
  implements EntityProvider<E, D>, Disposable
{

  private final Map<D, E> entities;

  public BaseEntityProvider( EntityFactory<E, D> entityFactory ) {
    entities = getStreamOfDefinitions()
      .collect( toMap( definition -> definition, definition -> entityFactory.create( definition ) ) );
  }

  @Override
  public Collection<E> findAll() {
    return new HashSet<>( entities.values() );
  }

  @Override
  public E findByDefinition( D definition ) {
    return entities.get( definition );
  }

  @Override
  public void dispose() {
    entities.clear();
  }

  protected abstract Stream<D> getStreamOfDefinitions();
}