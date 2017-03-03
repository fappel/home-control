package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSwitchStrategy.ZONE_ACTIVATION;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.codeaffine.home.control.application.ZoneActivation;
import com.codeaffine.home.control.application.control.ControlCenterOperation;
import com.codeaffine.home.control.application.control.Event;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class LampSwitchOperation implements ControlCenterOperation {

  private final EntityRegistry entityRegistry;

  private LampSwitchStrategy lampSwitchStrategy;
  private Predicate<Lamp> filter;

  public enum LampSwitchStrategy {
    ALL, NONE, ZONE_ACTIVATION
  }

  LampSwitchOperation( EntityRegistry entityRegistry ) {
    this.entityRegistry = entityRegistry;
    prepare();
  }

  public void setLampFilter( Predicate<Lamp> filter ) {
    verifyNotNull( filter, "filter" );

    this.filter = filter;
  }

  public void setLampSwitchStrategy( LampSwitchStrategy lampSwitchStrategy ) {
    verifyNotNull( lampSwitchStrategy, "lampSwitchStrategy" );

    this.lampSwitchStrategy = lampSwitchStrategy;
  }

  @Override
  public void prepare() {
    filter = lamp -> true;
    lampSwitchStrategy = ZONE_ACTIVATION;
  }

  @Override
  public void executeOn( Event event ) {
    event.getSource( ZoneActivation.class ).ifPresent( zoneActivation -> {
      Set<Lamp> on = collectLampsToSwitchOn( zoneActivation );
      Collection<Lamp> lamps = entityRegistry.findByDefinitionType( LampDefinition.class );
      Set<Lamp> off = lamps.stream().filter( bulb -> !on.contains( bulb ) ).collect( toSet() );
      on.forEach( lamp -> lamp.setOnOffStatus( ON ) );
      off.forEach( lamp -> lamp.setOnOffStatus( OFF ) );
    } );
  }

  private Set<Lamp> collectLampsToSwitchOn( ZoneActivation zoneActivation ) {
    Collection<Lamp> on = null;
    switch( lampSwitchStrategy ) {
      case ZONE_ACTIVATION:
        on = collectZoneLampsToSwitchOn( zoneActivation );
        break;
      case ALL:
        on = collectAllZoneLampsToSwitchOn();
        break;
      case NONE:
        on = emptySet();
        break;
      default:
        throw new IllegalStateException( "Uncovered LampActivationStrategy: " + lampSwitchStrategy );
    }
    return new HashSet<>( on );
  }

  private Set<Lamp> collectZoneLampsToSwitchOn( ZoneActivation zoneActivation ) {
    return zoneActivation
      .getStatus()
      .stream()
      .flatMap( zone -> getZoneLamps( zone ).stream() )
      .filter( filter )
      .collect( toSet() );
  }

  private static Collection<Lamp> getZoneLamps( Entity<EntityDefinition<?>> zone ) {
    return ( ( CompositeEntity<?> )zone ).getChildren( LampDefinition.class );
  }

  private Set<Lamp> collectAllZoneLampsToSwitchOn() {
    return entityRegistry.findByDefinitionType( LampDefinition.class )
        .stream()
        .filter( filter )
        .collect( toSet() );
  };
}