package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.HallCeiling;
import static com.codeaffine.home.control.application.operation.HomeControlOperations.isRelated;
import static com.codeaffine.home.control.application.operation.LampSwitchOperation.LampSelectionStrategy.ZONE_ACTIVATION;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.entity.EntityProvider.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.FollowUpTimer;
import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;

public class LampSwitchOperation implements HomeControlOperation {

  static final TimeUnit LAMP_DELAY_TIMEUNIT = SECONDS;
  static final long LAMP_DELAY_TIME = 2L;

  private final ActivationProvider zoneActivationProvider;
  private final Map<Lamp, Activation> delayStatus;
  private final EntityRegistry entityRegistry;
  private final FollowUpTimer followUpTimer;
  private final Set<Lamp> lampsToSwitchOff;
  private final Set<Lamp> lampsToSwitchOn;
  private final Set<Lamp> scheduled;
  private final Set<Lamp> delayed;

  private LampSelectionStrategy lampSelectionStrategy;
  private Predicate<Lamp> filter;

  public enum LampSelectionStrategy {
    ALL, NONE, ZONE_ACTIVATION
  }

  LampSwitchOperation(
    EntityRegistry entityRegistry, ActivationProvider zoneActivationProvider, FollowUpTimer followUpTimer )
  {
    verifyNotNull( zoneActivationProvider, "zoneActivationProvider" );
    verifyNotNull( entityRegistry, "entityRegistration" );
    verifyNotNull( followUpTimer, "followUpTimer" );

    this.zoneActivationProvider = zoneActivationProvider;
    this.entityRegistry = entityRegistry;
    this.followUpTimer = followUpTimer;
    this.lampsToSwitchOff = new HashSet<>();
    this.lampsToSwitchOn = new HashSet<>();
    this.delayStatus = new HashMap<>();
    this.scheduled = new HashSet<>();
    this.delayed = new HashSet<>();
    reset();
  }

  public void setLampSelectionStrategy( LampSelectionStrategy lampSelectionStrategy ) {
    verifyNotNull( lampSelectionStrategy, "lampSelectionStrategy" );

    this.lampSelectionStrategy = lampSelectionStrategy;
  }

  public void setLampFilter( Predicate<Lamp> filter ) {
    verifyNotNull( filter, "filter" );

    this.filter = filter;
  }

  public void setLampsToSwitchOn( LampDefinition ... lampsToSwitchOn ) {
    verifyNotNull( lampsToSwitchOn, "lampsToSwitchOn" );

    this.lampsToSwitchOn.clear();
    this.lampsToSwitchOn.addAll( mapToLamps( lampsToSwitchOn ) );
  }

  public void setLampsToSwitchOff( LampDefinition ... lampsToSwitchOff ) {
    verifyNotNull( lampsToSwitchOff, "lampsToSwitchOff" );

    this.lampsToSwitchOff.clear();
    this.lampsToSwitchOff.addAll( mapToLamps( lampsToSwitchOff ) );
  }

  public void setDelayed( LampDefinition ... delayed ) {
    verifyNotNull( delayed, "delayed" );

    this.delayed.clear();
    this.delayed.addAll( mapToLamps( delayed ) );
  }

  @Override
  public void reset() {
    setDelayed( HallCeiling );
    lampSelectionStrategy = ZONE_ACTIVATION;
    filter = lamp -> true;
    lampsToSwitchOff.clear();
    lampsToSwitchOn.clear();
    delayStatus.clear();
  }

  @Override
  public void executeOn( StatusEvent event ) {
    verifyNotNull( event, "event" );

    if( isRelated( event, NamedSceneProvider.class, ActivationProvider.class ) ) {
      execute();
    }
  }

  private void execute() {
    Set<Lamp> on = collectLampsToSwitchOn( zoneActivationProvider );
    Collection<Lamp> lamps = entityRegistry.findByDefinitionType( LampDefinition.class );
    Set<Lamp> off = lamps.stream().filter( lamp -> !on.contains( lamp ) ).collect( toSet() );
    on.forEach( lamp -> lamp.setOnOffStatus( ON ) );
    off.forEach( lamp -> lamp.setOnOffStatus( OFF ) );
  }

  private Set<Lamp> collectLampsToSwitchOn( ActivationProvider zoneActivation ) {
    Set<Lamp> strategyRelatedLampsToSwitchOn = collectStrategyRelatedLampsToSwitchOn( zoneActivation );
    strategyRelatedLampsToSwitchOn.addAll( lampsToSwitchOn );
    strategyRelatedLampsToSwitchOn.removeAll( lampsToSwitchOff );
    return filterDelayed( strategyRelatedLampsToSwitchOn, zoneActivation.getStatus() );
  }

  private Set<Lamp> collectStrategyRelatedLampsToSwitchOn( ActivationProvider zoneActivation ) {
    Collection<Lamp> on = null;
    switch( lampSelectionStrategy ) {
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
        throw new IllegalStateException( "Uncovered LampActivationStrategy: " + lampSelectionStrategy );
    }
    return new HashSet<>( on );
  }

  private Set<Lamp> collectZoneLampsToSwitchOn( ActivationProvider zoneActivation ) {
    return collectAffectedLamps( zoneActivation.getStatus() ).stream().filter( filter ).collect( toSet() );
  }

  private Set<Lamp> collectAllZoneLampsToSwitchOn() {
    return entityRegistry.findByDefinitionType( LampDefinition.class )
        .stream()
        .filter( filter )
        .collect( toSet() );
  }

  private Set<Lamp> filterDelayed( Set<Lamp> lampsToSwitchOn, Activation activation ) {
    Set<Lamp> result = lampsToSwitchOn;
    if( lampSelectionStrategy == ZONE_ACTIVATION ) {
      result = doFilterDelayed( lampsToSwitchOn, activation );
    }
    return result;
  }

  private Set<Lamp> doFilterDelayed( Set<Lamp> lampsToSwitchOn, Activation activation ) {
    adjustScheduledAndDelayedStatus( activation );
    Set<Lamp> result = filter( lampsToSwitchOn, activation );
    if( needDelayedActivationTimer( lampsToSwitchOn, result ) )  {
      scheduleDelayedActivationTimer( lampsToSwitchOn, activation );
    }
    return result;
  }

  private boolean needDelayedActivationTimer( Set<Lamp> lampsToSwitchOn, Set<Lamp> result ) {
    return !result.equals( lampsToSwitchOn ) && scheduled.isEmpty();
  }

  private void scheduleDelayedActivationTimer( Set<Lamp> lampsToSwitchOn, Activation activation ) {
    Map<Lamp, Activation> delayStatus = computeDelayStatus( lampsToSwitchOn, activation );
    delayStatus.keySet().forEach( lamp -> scheduled.add( lamp ) );
    followUpTimer.schedule( LAMP_DELAY_TIME, LAMP_DELAY_TIMEUNIT, () -> setDelayStatus( delayStatus ) );
  }

  private Set<Lamp> filter( Set<Lamp> lampsToSwitchOn, Activation activation ) {
    return lampsToSwitchOn.stream().filter( lamp -> canSwitchOn( activation, lamp ) ).collect( toSet() );
  }

  private boolean canSwitchOn( Activation activation, Lamp lamp ) {
    if( delayStatus.containsKey( lamp ) ) {
      return delayStatus.get( lamp ).equals( activation ) || lamp.getOnOffStatus() == ON;
    }
    return !delayed.contains( lamp ) || lamp.getOnOffStatus() == ON;
  }

  private void setDelayStatus( Map<Lamp, Activation> delayStatus ) {
    this.delayStatus.clear();
    delayStatus.forEach( ( key, value ) -> this.delayStatus.put( key, value ) );
  }

  private static Set<Lamp> collectAffectedLamps( Activation activation ) {
    return activation
      .getAllZones()
      .stream()
      .flatMap( zone -> collectZoneLamps( zone.getZoneEntity() ).stream() )
      .collect( toSet() );
  }

  private static Collection<Lamp> collectZoneLamps( Entity<?> zone ) {
    return ( ( CompositeEntity<?> )zone ).getChildren( LampDefinition.class );
  }

  private Collection<Lamp> collectLampsWithSameZone( Lamp lamp ) {
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

  private Map<Lamp, Activation> computeDelayStatus( Set<Lamp> lampsToSwitchOn, Activation activation ) {
    return lampsToSwitchOn
      .stream()
      .filter( lamp -> delayed.contains( lamp ) )
      .collect( toMap( lamp -> lamp, lamp -> activation ) );
  }

  private void adjustScheduledAndDelayedStatus( Activation activation ) {
    Set<Lamp> affectedLamps = collectAffectedLamps( activation );
    if( allActivationZonesHaveScheduledLamps( affectedLamps ) ) {
      scheduled.clear();
      delayed.clear();
    }
    if( !concernsTimerSchedule( affectedLamps ) ) {
      scheduled.clear();
    }
    if( ignoreDelayOnSingleActivationIfAffected( activation, affectedLamps ) ) {
      delayed.clear();
    }
  }

  private boolean allActivationZonesHaveScheduledLamps( Set<Lamp> affectedLamps ) {
    return affectedLamps.stream().allMatch( lamp -> belongsToZoneWithScheduledLamp( lamp ) );
  }

  private boolean belongsToZoneWithScheduledLamp( Lamp lamp ) {
    return collectLampsWithSameZone( lamp ).stream().anyMatch( zoneLamp -> scheduled.contains( zoneLamp ) );
  }

  private boolean concernsTimerSchedule( Set<Lamp> affectedLamps ) {
    return affectedLamps.stream().anyMatch( lamp -> scheduled.contains( lamp ) );
  }

  private boolean ignoreDelayOnSingleActivationIfAffected( Activation activation, Set<Lamp> affected ) {
    return activation.getAllZones().size() == 1 && belongToZonesWithDelayedLamps( affected );
  }

  private boolean belongToZonesWithDelayedLamps( Set<Lamp> affectedLamps ) {
    return affectedLamps
      .stream()
      .allMatch( lamp -> collectLampsOfZonesWithDelayedThatMatch( affectedLamps )
      .contains( lamp ) );
  }

  private Set<Lamp> collectLampsOfZonesWithDelayedThatMatch( Set<Lamp> affectedLamps ) {
    return delayed
      .stream()
      .flatMap( delayedLamp -> collectLampsWithSameZone( delayedLamp ).stream() )
      .filter( lampOfZoneWithDelayed -> affectedLamps.contains( lampOfZoneWithDelayed ) )
      .collect( toSet() );
  }

  private Set<Lamp> mapToLamps( LampDefinition... lampsToSwitchOn ) {
    return asList( lampsToSwitchOn )
      .stream()
      .map( definition -> entityRegistry.findByDefinition( definition ) )
      .collect( toSet() );
  }
}