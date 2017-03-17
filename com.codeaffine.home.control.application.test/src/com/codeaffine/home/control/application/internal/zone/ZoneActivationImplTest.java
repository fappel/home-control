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

public class ZoneActivationImplTest {

  private static final String ZONE_NAME = "zoneName";

  private ZoneActivationImpl activation;
  private PathAdjacency adjacency;
  private Entity<?> zone;

  @Before
  public void setUp() {
    zone = stubEntity( stubEntityDefinition( ZONE_NAME ) );
    adjacency = mock( PathAdjacency.class );
    activation = new ZoneActivationImpl( zone, adjacency );
  }

  @Test
  public void initialStatus() {
    assertThat( activation.getZone() ).isSameAs( zone );
    assertThat( activation.getReleaseTime() ).isEmpty();
    assertThat( activation.getInPathReleaseMarkTime() ).isEmpty();
    assertThat( activation.isAdjacentActivated() ).isFalse();
  }

  @Test
  public void markRelease() {
    activation.markRelease();
    Optional<LocalDateTime> actual = activation.getReleaseTime();

    assertThat( actual ).isNotEmpty();
  }

  @Test
  public void isAdjacentActivated() {
    when( adjacency.isAdjacentActivated( zone ) ).thenReturn( true );

    boolean actual = activation.isAdjacentActivated();

    assertThat( actual ).isTrue();
  }

  @Test
  public void markForInPathRelease() {
    activation.markForInPathRelease();
    Optional<LocalDateTime> actual = activation.getInPathReleaseMarkTime();

    assertThat( actual ).isNotEmpty();
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<ZoneActivationImpl> instance = EqualsTester.newInstance( activation );
    instance.assertImplementsEqualsAndHashCode();
    instance.assertEqual( new ZoneActivationImpl( zone, adjacency ),
                          new ZoneActivationImpl( zone, mock( PathAdjacency.class ) ) );
    instance.assertNotEqual( new ZoneActivationImpl( mock( Entity.class ), adjacency ),
                             new ZoneActivationImpl( zone, mock( PathAdjacency.class ) ) );

    PathAdjacency localAdjacency = mock( PathAdjacency.class );
    ZoneActivationImpl localActivation1 = new ZoneActivationImpl( zone, localAdjacency );
    localActivation1.markForInPathRelease();
    instance.assertEqual( localActivation1, new ZoneActivationImpl( zone, mock( PathAdjacency.class ) ) );

    when( localAdjacency.isAdjacentActivated( zone ) ).thenReturn( true );
    instance.assertEqual( localActivation1, new ZoneActivationImpl( zone, mock( PathAdjacency.class ) ) );
    instance.assertEqual( activation, localActivation1, new ZoneActivationImpl( zone, mock( PathAdjacency.class ) ) );

    ZoneActivationImpl localActivation2 = new ZoneActivationImpl( zone, localAdjacency );
    localActivation1.markRelease();
    localActivation2.markRelease();
    instance.assertEqual( localActivation1, localActivation2 );
    instance.assertNotEqual( localActivation1, new ZoneActivationImpl( zone, mock( PathAdjacency.class ) ) );
    instance.assertNotEqual( new ZoneActivationImpl( zone, mock( PathAdjacency.class ) ), localActivation1 );


    ZoneActivationImpl localActivation3 = new ZoneActivationImpl( zone, localAdjacency );
    localActivation3.markForInPathRelease();
    waitALittle();
    localActivation3.markRelease();
    instance.assertNotEqual( localActivation1, localActivation3 );
  }

  @Test
  public void toStringImplementation() {
    String actual = activation.toString();

    assertThat( actual ).contains( ZONE_NAME );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsZoneArgument() {
    new ZoneActivationImpl( null, adjacency );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsTraceArgument() {
    new ZoneActivationImpl( zone, null );
  }
}