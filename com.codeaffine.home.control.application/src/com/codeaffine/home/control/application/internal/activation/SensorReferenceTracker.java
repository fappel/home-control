package com.codeaffine.home.control.application.internal.activation;

import static com.codeaffine.home.control.application.type.OnOff.ON;
import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.SensorEvent;

class SensorReferenceTracker {

  private final Set<Path> paths;

  SensorReferenceTracker( Set<Path> paths ) {
    this.paths = paths;
  }

  boolean adjustSensorReferencesOnly( SensorEvent<OnOff> event ) {
    if( ON == event.getSensorStatus() ) {
      return adjustReferencesOnSensorActivation( event );
    }
    return adjustReferencesOnSensorDeactivation( event );
  }

  private boolean adjustReferencesOnSensorActivation( SensorEvent<OnOff> event ) {
    List<Zone> affected = collect( zone -> affectedZoneHasBeenActivatedByDifferentSensor( event, zone ) );
    boolean result = !affected.isEmpty();
    replaceAffected( affected, zone -> addActivationSensor( event, zone ) );
    return result;
  }

  private boolean adjustReferencesOnSensorDeactivation( SensorEvent<OnOff> event ) {
    List<Zone> affected = collect( zone -> affectedZoneHasBeenActivatedByMoreThanTheEventsSensor( event, zone ) );
    boolean result = !affected.isEmpty();
    replaceAffected( affected, zone -> removeActivationSensor( event, zone ) );
    if( !result ) {
      result = streamOfZones().anyMatch( zone -> affectedZoneHasBeenActivatedByDifferentSensor( event, zone ) );
    }
    return result;
  }

  private List<Zone> collect( Predicate<? super Zone> predicate ) {
    return streamOfZones().filter( predicate ).collect( toList() );
  }

  private Stream<Zone> streamOfZones() {
    return paths.stream().flatMap( path -> path.getAll().stream() );
  }

  private static boolean affectedZoneHasBeenActivatedByDifferentSensor( SensorEvent<OnOff> event, Zone zone ) {
    return    event.getAffected().contains( zone.getZoneEntity() )
           && !( ( ZoneImpl )zone ).getActivationSensors().contains( ( event.getSensor() ) );
  }

  private Set<Path> collectPathsContainingZone( Zone zone ) {
    return paths.stream().filter( path -> doesPathContainZone( path, zone ) ).collect( toSet() );
  }

  private static boolean doesPathContainZone( Path path, Zone zone ) {
    return !path.findZoneActivation( zone.getZoneEntity() ).isEmpty();
  }

  private static ZoneImpl addActivationSensor( SensorEvent<OnOff> event, Zone zone ) {
    return ( ( ZoneImpl )zone ).addActivationSensor( event.getSensor() );
  }

  private static boolean affectedZoneHasBeenActivatedByMoreThanTheEventsSensor( SensorEvent<OnOff> event, Zone zone ) {
    return    event.getAffected().contains( zone.getZoneEntity() )
           && ( ( ZoneImpl )zone ).getActivationSensors().contains( ( event.getSensor() ) )
           && ( ( ZoneImpl )zone ).getActivationSensors().size() > 1;
  }

  private static ZoneImpl removeActivationSensor( SensorEvent<OnOff> event, Zone zone ) {
    return ( ( ZoneImpl )zone ).removeActivationSensor( event.getSensor() );
  }

  private void replaceAffected( List<Zone> affected, Function<Zone, Zone> replacer ) {
    affected.forEach( zone -> collectPathsContainingZone( zone ).forEach( path -> replace( path, zone, replacer ) ) );
  }

  private static void replace( Path path, Zone zone, Function<Zone, Zone> replacer ) {
    path.addOrReplace( replacer.apply( zone ) );
  }
}