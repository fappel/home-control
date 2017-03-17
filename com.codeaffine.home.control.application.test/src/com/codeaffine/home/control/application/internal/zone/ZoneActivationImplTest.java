package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.test.util.lang.EqualsTester;

public class ZoneActivationImplTest {

  private ZoneActivationImpl activation;
  private Path trace;
  private Entity<?> zone;

  @Before
  public void setUp() {
    zone = stubEntity( stubEntityDefinition( "entity" ) );
    trace = new Path();
    activation = new ZoneActivationImpl( zone, trace );
  }

  @Test
  public void initialStatus() {
    assertThat( activation.getZone() ).isSameAs( zone );
    assertThat( activation.getReleaseTime() ).isEmpty();
    assertThat( activation.getInPathReleaseMarkTime() ).isEmpty();
    assertThat( activation.hasAdjacentActivation() ).isFalse();
  }

  @Test
  public void markRelease() {
    activation.markRelease();
    Optional<LocalDateTime> actual = activation.getReleaseTime();

    assertThat( actual ).isNotEmpty();
  }

  @Test
  public void hasAdjacentActivation() {
    trace.addOrReplace( activation );
    trace.addOrReplace( mock( ZoneActivation.class ) );

    boolean actual = activation.hasAdjacentActivation();

    assertThat( actual ).isTrue();
  }

  @Test
  public void hasAdjacentActivationIfCurrentActivationIsOnlyTraceElement() {
    trace.addOrReplace( activation );

    boolean actual = activation.hasAdjacentActivation();

    assertThat( actual ).isFalse();
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
    instance.assertEqual( new ZoneActivationImpl( zone, trace ), new ZoneActivationImpl( zone, new Path() ) );
    instance.assertNotEqual( new ZoneActivationImpl( mock( Entity.class ), trace ),
                             new ZoneActivationImpl( zone, new Path() ) );

    Path localTrace = new Path();
    ZoneActivationImpl localActivation1 = new ZoneActivationImpl( zone, localTrace );
    localActivation1.markForInPathRelease();
    instance.assertEqual( localActivation1, new ZoneActivationImpl( zone, new Path() ) );

    localTrace.addOrReplace( localActivation1 );
    localTrace.addOrReplace( mock( ZoneActivation.class ) );
    instance.assertEqual( localActivation1, new ZoneActivationImpl( zone, new Path() ) );
    instance.assertEqual( activation, localActivation1, new ZoneActivationImpl( zone, new Path() ) );

    ZoneActivationImpl localActivation2 = new ZoneActivationImpl( zone, localTrace );
    localActivation1.markRelease();
    localActivation2.markRelease();
    instance.assertEqual( localActivation1, localActivation2 );
    instance.assertNotEqual( localActivation1, new ZoneActivationImpl( zone, new Path() ) );
    instance.assertNotEqual( new ZoneActivationImpl( zone, new Path() ), localActivation1 );


    ZoneActivationImpl localActivation3 = new ZoneActivationImpl( zone, localTrace );
    localActivation3.markForInPathRelease();
    waitALittle();
    localActivation3.markRelease();
    instance.assertNotEqual( localActivation1, localActivation3 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsZoneArgument() {
    new ZoneActivationImpl( null, trace );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsTraceArgument() {
    new ZoneActivationImpl( zone, null );
  }
}