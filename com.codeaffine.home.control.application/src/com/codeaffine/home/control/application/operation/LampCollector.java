package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.operation.Messages.ERROR_UNKNOWN_LAMP_SELECTION_STRATEGY;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class LampCollector {

  private final ActivationProvider activationProvider;
  private final EntityRegistry entityRegistry;

  public LampCollector( EntityRegistry entityRegistry, ActivationProvider activationProvider ) {
    verifyNotNull( activationProvider, "activationProvider" );
    verifyNotNull( entityRegistry, "entityRegistry" );

    this.activationProvider = activationProvider;
    this.entityRegistry = entityRegistry;
  }

  public Set<Lamp> collect( LampSelectionStrategy strategy ) {
    verifyNotNull( strategy, "strategy" );

    switch( strategy ) {
      case ZONE_ACTIVATION:
        return collectActivatedZoneLamps();
      case ALL:
        return collectAllLamps();
      case NONE:
        return emptySet();
      default:
        throw new IllegalStateException( format( ERROR_UNKNOWN_LAMP_SELECTION_STRATEGY, strategy ) );
    }
  }

  public Set<Lamp> collectAllLamps() {
    return new HashSet<>( entityRegistry.findByDefinitionType( LampDefinition.class ) );
  }

  public Set<Lamp> collectWithSameZone( Lamp lamp ) {
    verifyNotNull( lamp, "lamp" );

    return entityRegistry
      .findAll()
      .stream()
      .filter( entity -> entity instanceof CompositeEntity<?> )
      .filter( entity -> ( ( CompositeEntity<?> )entity ).getChildren().contains( lamp ) )
      .flatMap( entity -> ( ( CompositeEntity<?> )entity ).getChildren().stream() )
      .filter( child -> child instanceof Lamp )
      .map( child -> ( Lamp )child )
      .collect( toSet() );
  }

  public Set<Lamp> collectActivatedZoneLamps() {
    return activationProvider
      .getStatus()
      .getAllZones()
      .stream()
      .flatMap( zone -> collectZoneLamps( zone.getZoneEntity() ).stream() )
      .collect( toSet() );
  }

  public Lamp findByDefinition( LampDefinition definition ) {
    verifyNotNull( definition, "definition" );

    return entityRegistry.findByDefinition( definition );
  }

  public Set<Lamp> collectZoneLamps( EntityDefinition<?> zoneEntityDefinition ) {
    verifyNotNull( zoneEntityDefinition, "zoneEntityDefinition" );

    Set<Lamp> result = new HashSet<>();
    findZoneEntity( zoneEntityDefinition ).ifPresent( entity -> result.addAll( collectZoneLamps( entity ) ) );
    return result;
  }

  private Optional<Entity<?>> findZoneEntity( EntityDefinition<?> zoneEntityDefinition ) {
    return entityRegistry
      .findAll()
      .stream()
      .filter( entity -> entity.getDefinition() == zoneEntityDefinition )
      .findAny();
  }

  private static Collection<Lamp> collectZoneLamps( Entity<?> zoneEntity ) {
    if( zoneEntity instanceof CompositeEntity<?> ) {
      return ( ( CompositeEntity<?> )zoneEntity ).getChildren( LampDefinition.class );
    }
    return emptySet();
  }
}