package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
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
  }

  @Test
  public void initialStatus() {
    assertThat( zone.getZoneEntity() ).isSameAs( zoneEntity );
    assertThat( zone.getReleaseTime() ).isEmpty();
    assertThat( zone.getInPathReleaseMarkTime() ).isEmpty();
    assertThat( zone.isAdjacentActivated() ).isFalse();
  }

  @Test
  public void markAsReleased() {
    zone.markAsReleased();
    Optional<LocalDateTime> actual = zone.getReleaseTime();

    assertThat( actual ).isNotEmpty();
  }

  @Test
  public void isAdjacentActivated() {
    when( adjacency.isAdjacentActivated( zoneEntity ) ).thenReturn( true );

    boolean actual = zone.isAdjacentActivated();

    assertThat( actual ).isTrue();
  }

  @Test
  public void markForInPathRelease() {
    zone.markForInPathRelease();
    Optional<LocalDateTime> actual = zone.getInPathReleaseMarkTime();

    assertThat( actual ).isNotEmpty();
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
    ZoneImpl localZone1 = new ZoneImpl( zoneEntity, localAdjacency );
    localZone1.markForInPathRelease();
    instance.assertEqual( localZone1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );

    when( localAdjacency.isAdjacentActivated( zoneEntity ) ).thenReturn( true );
    instance.assertEqual( localZone1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );
    instance.assertEqual( zone, localZone1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );

    ZoneImpl localZone2 = new ZoneImpl( zoneEntity, localAdjacency );
    localZone1.markAsReleased();
    localZone2.markAsReleased();
    instance.assertEqual( localZone1, localZone2 );
    instance.assertNotEqual( localZone1, new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ) );
    instance.assertNotEqual( new ZoneImpl( zoneEntity, mock( PathAdjacency.class ) ), localZone1 );


    ZoneImpl localZone3 = new ZoneImpl( zoneEntity, localAdjacency );
    localZone3.markForInPathRelease();
    waitALittle();
    localZone3.markAsReleased();
    instance.assertNotEqual( localZone1, localZone3 );
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
}