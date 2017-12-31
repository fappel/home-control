package com.codeaffine.home.control.status.internal.activation;

import static com.codeaffine.home.control.status.internal.activation.PreferenceUtil.*;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.*;
import static com.codeaffine.home.control.status.type.OnOff.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.status.internal.activation.Path;
import com.codeaffine.home.control.status.internal.activation.SensorReferenceTracker;
import com.codeaffine.home.control.status.internal.activation.ZoneImpl;
import com.codeaffine.home.control.status.model.ActivationEvent;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.type.OnOff;
import com.codeaffine.home.control.entity.Sensor;

public class SensorReferenceTrackerTest {

  private SensorReferenceTracker tracker;
  private Set<Path> paths;
  private Sensor sensor1;
  private Sensor sensor2;

  @Before
  public void setUp() {
    paths = new HashSet<>();
    tracker = new SensorReferenceTracker( paths );
    sensor1 = mock( Sensor.class );
    sensor2 = mock( Sensor.class );
  }

  @Test
  public void adjustSensorReferencesOnlyOnDifferentSensorActivation() {
    addOrReplaceInNewPath( createZone( ZONE_1, sensor1 ) );
    addOrReplaceInNewPath( createZone( ZONE_3, sensor1 ) );

    boolean actual = tracker.adjustSensorReferencesOnly( newEvent( sensor2, ON, ZONE_1 ) );

    assertThat( actual ).isTrue();
    assertThat( paths ).allMatch( path -> path.getAll().size() == 1 );
    assertThat( findZone( ZONE_1 ).getActivationSensors() )
      .hasSize( 2 )
      .contains( sensor1, sensor2 );
    assertThat( findZone( ZONE_3 ).getActivationSensors() )
      .hasSize( 1 )
      .contains( sensor1 );
  }

  @Test
  public void adjustSensorReferencesOnlyOnWithNewZoneActivation() {
    boolean actual = tracker.adjustSensorReferencesOnly( newEvent( sensor1, ON, ZONE_1 ) );

    assertThat( actual ).isFalse();
    assertThat( paths ).isEmpty();
  }

  @Test
  public void adjustSensorReferencesOnlyOnDeactivationOfOneOfMultiActivations() {
    addOrReplaceInNewPath( createZone( ZONE_1, sensor1 ) );

    tracker.adjustSensorReferencesOnly( newEvent( sensor2, ON, ZONE_1 ) );
    boolean actual = tracker.adjustSensorReferencesOnly( newEvent( sensor2, OFF, ZONE_1 ) );

    assertThat( actual ).isTrue();
    assertThat( findZone( ZONE_1 ).getActivationSensors() )
      .hasSize( 1 )
      .contains( sensor1 );
  }

  @Test
  public void adjustSensorReferencesOnlyOnDeactivationByTheOneAndOnlyActivationSensor() {
    addOrReplaceInNewPath( createZone( ZONE_1, sensor1 ) );

    boolean actual = tracker.adjustSensorReferencesOnly( newEvent( sensor1, OFF, ZONE_1 ) );

    assertThat( actual ).isFalse();
    assertThat( findZone( ZONE_1 ).getActivationSensors() )
      .hasSize( 1 )
      .contains( sensor1 );
  }

  @Test
  public void adjustSensorReferencesOnlyOnDeactivationByADisjointActivationSensor() {
    addOrReplaceInNewPath( createZone( ZONE_1, sensor1 ) );

    boolean actual = tracker.adjustSensorReferencesOnly( newEvent( sensor2, OFF, ZONE_1 ) );

    assertThat( actual ).isTrue();
    assertThat( findZone( ZONE_1 ).getActivationSensors() )
      .hasSize( 1 )
      .contains( sensor1 );
  }

  @Test
  public void adjustSensorReferencesOnlyOnDeactivationOfUnrelatedZone() {
    addOrReplaceInNewPath( createZone( ZONE_1, sensor1 ) );

    boolean actual = tracker.adjustSensorReferencesOnly( newEvent( sensor1, OFF, ZONE_2 ) );

    assertThat( actual ).isFalse();
    assertThat( findZone( ZONE_1 ).getActivationSensors() )
      .hasSize( 1 )
      .contains( sensor1 );
  }

  @Test
  public void adjustSensorReferencesOnlyOnDeactivationOfUnrelatedZoneAndUnrelatedSensor() {
    addOrReplaceInNewPath( createZone( ZONE_1, sensor1 ) );

    boolean actual = tracker.adjustSensorReferencesOnly( newEvent( sensor2, OFF, ZONE_2 ) );

    assertThat( actual ).isFalse();
    assertThat( findZone( ZONE_1 ).getActivationSensors() )
      .hasSize( 1 )
      .contains( sensor1 );
  }

  private ZoneImpl findZone( Entity<EntityDefinition<?>> toFind ) {
    return ( ZoneImpl )paths
      .stream()
      .flatMap( path -> path.getAll().stream() )
      .filter( zone -> zone.getZoneEntity() == toFind )
      .findFirst()
      .get();
  }

  @SafeVarargs
  private static ActivationEvent newEvent(
    Sensor sensor, OnOff sensorStatus, Entity<EntityDefinition<?>> ... affected )
  {
    return new ActivationEvent( sensor, sensorStatus, affected );
  }

  private Path addOrReplaceInNewPath( Zone ... zones ) {
    Path result = new Path( stubPreference( PATH_EXPIRED_TIMEOUT_IN_SECONDS ) );
    Stream.of( zones ).forEach( zone  -> result.addOrReplace( zone ) );
    paths.add( result );
    return result;
  }
}