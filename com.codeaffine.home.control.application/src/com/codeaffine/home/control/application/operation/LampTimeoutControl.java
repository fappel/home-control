package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.operation.LampTimeoutModus.*;
import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.application.util.TimeoutPreference;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;

class LampTimeoutControl {

  private final Set<Set<SectionDefinition>> relatedSections;
  private final Map<Lamp, Timeout> keepLampsAliveTimeouts;
  private final ActivationSupplier activationSupplier;
  private final LampCollector lampCollector;
  private final Set<Lamp> lampsToSwitchOff;
  private final Set<Lamp> switchedOn;

  private Supplier<Timeout> timeoutSupplier;
  private LampTimeoutModus timeoutModus;

  LampTimeoutControl(
    ActivationSupplier activationSupplier,
    LampCollector lampCollector,
    LampSwitchOperationPreference preference )
  {
    this.activationSupplier = activationSupplier;
    this.lampCollector = lampCollector;
    this.timeoutSupplier = () -> createHotTimeout( preference );
    this.keepLampsAliveTimeouts = new HashMap<>();
    this.lampsToSwitchOff = new HashSet<>();
    this.relatedSections = new HashSet<>();
    this.switchedOn = new HashSet<>();
    this.timeoutModus = OFF;
  }

  void setTimeoutModus( LampTimeoutModus timeoutModus ) {
    this.timeoutModus = timeoutModus;
  }

  void setTimeoutSupplier( Supplier<Timeout> timeoutSupplier ) {
    this.timeoutSupplier = timeoutSupplier;
  }

  void addGroupOfRelatedSections( Set<SectionDefinition> relatedSections ) {
    this.relatedSections.add( relatedSections );
  }

  void addLampsToSwitchOff( Set<Lamp> lampsToSwitchOff ) {
    this.lampsToSwitchOff.addAll( lampsToSwitchOff );
  }

  Set<Lamp> getLampsToSwitchOff() {
    return new HashSet<>( lampsToSwitchOff );
  }

  void prepare() {
    lampsToSwitchOff.clear();
    relatedSections.clear();
  }

  void setLampsToSwitchOn( Set<Lamp> lampsToSwitchOn ) {
    Set<Lamp> previouslySwitchedOn = new HashSet<>( switchedOn );
    switchedOn.clear();
    switchedOn.addAll( lampsToSwitchOn );
    if( isKeepLampsAliveTimoutModusOn() ) {
      updateKeepLampsAliveTimouts( previouslySwitchedOn );
    }
  }

  Set<Lamp> getLampsToSwitchOn() {
    Set<Lamp> result = new HashSet<>( switchedOn );
    if( isKeepLampsAliveTimoutModusOn() ) {
      result.addAll( collectLampsToKeepAlive() );
    }
    return result;
  }

  private void updateKeepLampsAliveTimouts( Set<Lamp> previouslySwitchedOn ) {
    removeCurrentlySwitchedOn( previouslySwitchedOn );
    previouslySwitchedOn.forEach( lamp -> keepLampsAliveTimeouts.put( lamp, timeoutSupplier.get() ) );
    updateKeepLampAliveTimeouts();
  }

  private void updateKeepLampAliveTimeouts() {
    keepLampsAliveTimeouts
      .keySet()
      .stream()
      .collect( toSet() )
      .stream()
      .filter( lamp -> lampsToSwitchOff.contains( lamp ) || keepLampsAliveTimeouts.get( lamp ).isExpired() )
      .forEach( lamp -> keepLampsAliveTimeouts.remove( lamp ) );
  }

  private void removeCurrentlySwitchedOn( Set<Lamp> previouslySwitchedOn ) {
    switchedOn
      .stream()
      .filter( lamp -> previouslySwitchedOn.contains( lamp ) )
      .forEach( lamp -> previouslySwitchedOn.remove( lamp ) );
  }

  private Set<Lamp> collectLampsToKeepAlive() {
    return keepLampsAliveTimeouts
      .keySet()
      .stream()
      .filter( lamp -> !collectLampsOfActiveZones().contains( lamp ) )
      .filter( lamp -> !keepLampsAliveTimeouts.get( lamp ).isExpired() )
      .collect( toSet() );
  }

  private Set<Lamp> collectLampsOfActiveZones() {
    return activationSupplier
      .getStatus()
      .getAllZones()
      .stream()
      .flatMap( zone -> getStreamOfRelatedZoneLamps( zone ) )
      .collect( toSet() );
  }

  private Stream<Lamp> getStreamOfRelatedZoneLamps( Zone zone ) {
    EntityDefinition<?> activeSectionDefinition = ( EntityDefinition<?> )zone.getZoneEntity().getDefinition();
    Set<Lamp> related = collectRelatedSections( activeSectionDefinition );
    related.addAll( lampCollector.collectZoneLamps( activeSectionDefinition ) );
    return related.stream();
  }

  private Set<Lamp> collectRelatedSections( EntityDefinition<?> activeSectionDefinition ) {
    return relatedSections
      .stream()
      .filter( definitions -> definitions.contains( activeSectionDefinition ) )
      .flatMap( definitions -> definitions.stream() )
      .flatMap( definition -> lampCollector.collectZoneLamps( definition ).stream() )
      .collect( toSet() );
  }

  private boolean isKeepLampsAliveTimoutModusOn() {
    return timeoutModus == ON;
  }

  static Timeout createHotTimeout( TimeoutPreference preference ) {
    Timeout result = new Timeout( preference );
    result.set();
    return result;
  }
}