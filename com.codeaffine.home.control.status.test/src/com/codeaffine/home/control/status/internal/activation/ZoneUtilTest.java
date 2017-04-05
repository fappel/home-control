package com.codeaffine.home.control.status.internal.activation;

import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.status.internal.activation.Path;
import com.codeaffine.home.control.status.internal.activation.PathAdjacency;
import com.codeaffine.home.control.status.internal.activation.ZoneImpl;
import com.codeaffine.home.control.status.internal.activation.ZoneUtil;

public class ZoneUtilTest {

  private PathAdjacency adjacency;
  private ZoneUtil util;

  @Before
  public void setUp() {
    adjacency = mock( PathAdjacency.class );
    util = new ZoneUtil( adjacency );
  }

  @Test
  public void newZone() {
    when( adjacency.isAdjacentActivated( ZONE_1 ) ).thenReturn( true );
    Sensor sensor = mock( Sensor.class );

    ZoneImpl actual = util.newZone( ZONE_1, sensor );

    assertThat( actual.getActivationSensors() ).containsExactly( sensor );
    assertThat( actual.isAdjacentActivated() ).isTrue();
  }

  @Test
  public void markAsReleased() {
    Sensor sensor = mock( Sensor.class );
    ZoneImpl roRelease = util.newZone( ZONE_1, sensor );
    Path path = new Path();

    util.markAsReleased( roRelease, path );

    assertThat( path.getAll() )
      .hasSize( 1 )
      .allMatch( zone -> zone.getReleaseTime().isPresent() )
      .allMatch( zone -> zone.getZoneEntity() == ZONE_1 );
  }

  @Test
  public void markForInPathRelease() {
    Path path = new Path();
    path.addOrReplace( createZone( ZONE_1 ) );

    ZoneUtil.markForInPathRelease( ZONE_1, path );

    assertThat( path.getAll() )
      .hasSize( 1 )
      .allMatch( zone -> ( ( ZoneImpl )zone ).getInPathReleaseMarkTime().isPresent() )
      .allMatch( zone -> zone.getZoneEntity() == ZONE_1 );
  }
}