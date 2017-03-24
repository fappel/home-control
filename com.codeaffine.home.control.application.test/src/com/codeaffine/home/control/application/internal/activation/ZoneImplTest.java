package com.codeaffine.home.control.application.internal.activation;

import static com.codeaffine.home.control.application.internal.activation.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.test.util.lang.EqualsTester;

public class ZoneImplTest {

  private static final String ZONE_NAME = "zoneName";

  private PathAdjacency adjacency;
  private Entity<?> zoneEntity;
  private ZoneImpl zone;

  @Before
  public void setUp() {
    zoneEntity = stubEntity( stubEntityDefinition( ZONE_NAME ) );
    adjacency = mock( PathAdjacency.class );
    zone = new ZoneImpl( zoneEntity, adjacency );
    stubZonesOfPath( zone );
  }

  @Test
  public void initialStatus() {
    assertThat( zone.getZoneEntity() ).isSameAs( zoneEntity );
    assertThat( zone.getReleaseTime() ).isEmpty();
    assertThat( zone.getInPathReleaseMarkTime() ).isEmpty();
    assertThat( zone.isAdjacentActivated() ).isFalse();
    assertThat( zone.getActivationSensors() ).isEmpty();
    assertThat( zone.getZonesOfRelatedPaths() ).containsExactly( zone );
  }

  @Test
  public void markAsReleased() {
    ZoneImpl markedAsReleased = zone.markForInPathRelease().markAsReleased();
    Optional<LocalDateTime> originalReleaseTime = zone.getReleaseTime();
    Optional<LocalDateTime> actualReleaseTime = markedAsReleased.getReleaseTime();
    Optional<LocalDateTime> actualInPathReleaseMarkTime = markedAsReleased.getInPathReleaseMarkTime();

    assertThat( originalReleaseTime ).isEmpty();
    assertThat( actualReleaseTime ).isNotEmpty();
    assertThat( actualInPathReleaseMarkTime ).isNotEmpty();
  }

  @Test
  public void getZonesOfRelatedPaths() {
    stubZonesOfPath( zone, createZone( ZONE_1 ) );

    Set<Zone> actual = zone.getZonesOfRelatedPaths();

    assertThat( actual )
      .hasSize( 2 )
      .contains( zone, createZone( ZONE_1 ) );
  }

  @Test
  public void isAdjacentActivated() {
    when( adjacency.isAdjacentActivated( zoneEntity ) ).thenReturn( true );

    boolean actual = zone.isAdjacentActivated();

    assertThat( actual ).isTrue();
  }

  @Test
  public void markForInPathRelease() {
    ZoneImpl markedForInPathRelease = zone.markAsReleased().markForInPathRelease();
    Optional<LocalDateTime> originalInPathReleaseMarkTime = zone.getInPathReleaseMarkTime();
    Optional<LocalDateTime> actualInPathReleaseMarkTime = markedForInPathRelease.getInPathReleaseMarkTime();
    Optional<LocalDateTime> actualReleaseTime = markedForInPathRelease.getReleaseTime();

    assertThat( originalInPathReleaseMarkTime ).isEmpty();
    assertThat( actualInPathReleaseMarkTime ).isNotEmpty();
    assertThat( actualReleaseTime ).isNotEmpty();
  }

  @Test
  public void addActivitySensor() {
    Sensor expected = mock( Sensor.class );

    ZoneImpl actual = zone.markAsReleased().markForInPathRelease().addActivationSensor( expected );
    Set<Sensor> newSensors = actual.getActivationSensors();
    Set<Sensor> originalSensors = zone.getActivationSensors();

    assertThat( originalSensors ).isEmpty();
    assertThat( actual.getReleaseTime() ).isEmpty();
    assertThat( actual.getInPathReleaseMarkTime() ).isEmpty();
    assertThat( newSensors ).containsExactly( expected );
  }

  @Test
  public void removeActivitySensor() {
    Sensor sensor1 = mock( Sensor.class );
    Sensor sensor2 = mock( Sensor.class );
    ZoneImpl zoneWithSensors = zone.addActivationSensor( sensor1 ).addActivationSensor( sensor2 );

    ZoneImpl actual = zoneWithSensors.markAsReleased().markForInPathRelease().removeActivationSensor( sensor1 );
    Set<Sensor> newSensors = actual.getActivationSensors();

    assertThat( actual.getReleaseTime() ).isEmpty();
    assertThat( actual.getInPathReleaseMarkTime() ).isEmpty();
    assertThat( newSensors ).containsExactly( sensor2 );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<ZoneImpl> instance = EqualsTester.newInstance( zone );
    instance.assertImplementsEqualsAndHashCode();
    instance.assertEqual( new ZoneImpl( zoneEntity, adjacency ),
                          new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );
    instance.assertNotEqual( new ZoneImpl( mock( Entity.class ), adjacency ),
                             new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );

    PathAdjacency localAdjacency = mock( PathAdjacency.class );
    ZoneImpl localZone1 = new ZoneImpl( zoneEntity, localAdjacency ).markForInPathRelease();
    instance.assertEqual( localZone1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );

    when( localAdjacency.isAdjacentActivated( zoneEntity ) ).thenReturn( true );
    instance.assertEqual( localZone1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );
    instance.assertEqual( zone, localZone1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );

    ZoneImpl markAsReleased1 = localZone1.markAsReleased();
    ZoneImpl markAsReleased2 = new ZoneImpl( zoneEntity, localAdjacency ).markAsReleased();
    instance.assertEqual( markAsReleased1, markAsReleased2 );
    instance.assertNotEqual( markAsReleased1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );
    instance.assertNotEqual( new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ), markAsReleased1 );
    instance.assertNotEqual( markAsReleased1, localZone1 );

    ZoneImpl markedForInPathRelease = new ZoneImpl( zoneEntity, localAdjacency ).markForInPathRelease();
    waitALittle();
    ZoneImpl markedAsReleased = markedForInPathRelease.markAsReleased();
    instance.assertNotEqual( markedAsReleased, new ZoneImpl( zoneEntity, localAdjacency ) );
  }

  @Test
  public void toStringImplementation() {
    String actual = zone.toString();

    assertThat( actual ).contains( ZONE_NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsZoneArgument() {
    new ZoneImpl( null, adjacency );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsTraceArgument() {
    new ZoneImpl( zoneEntity, null );
  }

  private void stubZonesOfPath( Zone zone, ZoneImpl ... relatedPathZones ) {
    Set<Zone> zones = asSet( relatedPathZones );
    zones.add( zone );
    when( adjacency.getZonesOfRelatedPaths( zone ) ).thenReturn( zones );
  }
}