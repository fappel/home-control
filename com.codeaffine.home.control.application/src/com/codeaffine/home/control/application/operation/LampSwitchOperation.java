package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSwitchSelectionStrategy.ZONE_ACTIVATION;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.codeaffine.home.control.application.control.ControlCenterOperation;
import com.codeaffine.home.control.application.control.StatusEvent;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class LampSwitchOperation implements ControlCenterOperation {

  private final EntityRegistry entityRegistry;

  private LampSwitchSelectionStrategy lampSwitchSelectionStrategy;
  private Predicate<Lamp> filter;

  public enum LampSwitchSelectionStrategy {
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

  public void setLampSwitchSelectionStrategy( LampSwitchSelectionStrategy lampSwitchSelectionStrategy ) {
    verifyNotNull( lampSwitchSelectionStrategy, "lampSwitchSelectionStrategy" );

    this.lampSwitchSelectionStrategy = lampSwitchSelectionStrategy;
  }

  @Override
  public void prepare() {
    filter = lamp -> true;
    lampSwitchSelectionStrategy = ZONE_ACTIVATION;
  }

  @Override
  public void executeOn( StatusEvent event ) {
    event.getSource( ZoneActivationProvider.class ).ifPresent( zoneActivation -> {
      Set<Lamp> on = collectLampsToSwitchOn( zoneActivation );
      Collection<Lamp> lamps = entityRegistry.findByDefinitionType( LampDefinition.class );
      Set<Lamp> off = lamps.stream().filter( lamp -> !on.contains( lamp ) ).collect( toSet() );
      on.forEach( lamp -> lamp.setOnOffStatus( ON ) );
      off.forEach( lamp -> lamp.setOnOffStatus( OFF ) );
    } );
  }

  private Set<Lamp> collectLampsToSwitchOn( ZoneActivationProvider zoneActivation ) {
    Collection<Lamp> on = null;
    switch( lampSwitchSelectionStrategy ) {
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
        throw new IllegalStateException( "Uncovered LampActivationStrategy: " + lampSwitchSelectionStrategy );
    }
    return new HashSet<>( on );
  }

  private Set<Lamp> collectZoneLampsToSwitchOn( ZoneActivationProvider zoneActivation ) {
    return zoneActivation
      .getStatus()
      .stream()
      .flatMap( activation -> getZoneLamps( activation.getZone() ).stream() )
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