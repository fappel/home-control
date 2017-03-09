package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.AllocationTracker.Sensor;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.util.Disposable;

public class EntityRelationProviderImpl implements EntityRelationProvider, Disposable {

  private final Map<EntityDefinition<?>, Collection<EntityDefinition<?>>> entityRelations;
  private final EntityRegistry entityRegistry;

  public EntityRelationProviderImpl( EntityRegistry entityRegistry ) {
    this.entityRegistry = entityRegistry;
    this.entityRelations = new HashMap<>();
  }

  @Override
  public void dispose() {
    entityRelations.clear();
  }

  public void establishRelations( EntityRelationConfiguration configuration ) {
    verifyNotNull( configuration, "configuration" );

    configuration.configureFacility( parent -> children -> entityRelations.put( parent, asList( children ) ) );
    registerZonesAtSensorChildren();
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>> E findByDefinition( D definition ) {
    verifyNotNull( definition, "definition" );

    return entityRegistry.findByDefinition( definition );
  }

  @Override
  public Collection<Entity<?>> findAll() {
    return entityRegistry.findAll();
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>>
    Collection<E> findByDefinitionType( Class<D> definitionType )
  {
    verifyNotNull( definitionType, "definitionType" );

    return entityRegistry.findByDefinitionType( definitionType );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E extends Entity<D>, D extends EntityDefinition<E>, P extends EntityDefinition<?>>
    Collection<D> getChildren( P parentDefinition, Class<D> childDefinitionType )
  {
    verifyNotNull( childDefinitionType, "childDefinitionType" );
    verifyNotNull( parentDefinition, "parentDefinition" );

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

  private void registerZonesAtSensorChildren() {
    entityRegistry
      .findAll()
      .stream()
      .filter( entity -> entity instanceof CompositeEntity )
      .map( entity -> ( CompositeEntity<?> )entity )
      .forEach( composite -> registerZoneAtSensorChild( composite ) );
  }

  private static void registerZoneAtSensorChild( CompositeEntity<?> zone ) {
    zone.getChildren()
      .stream()
      .filter( child -> child instanceof Sensor )
      .map( child -> ( Sensor )child )
      .forEach( sensor -> sensor.registerAllocable( zone ) );
  }
}