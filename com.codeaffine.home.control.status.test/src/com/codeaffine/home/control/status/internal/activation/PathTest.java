package com.codeaffine.home.control.status.internal.activation;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.status.internal.activation.ActivationSupplierImpl.PATH_EXPIRED_TIMEOUT;
import static com.codeaffine.home.control.status.internal.activation.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.*;
import static com.codeaffine.test.util.lang.EqualsTester.newInstance;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.test.util.lang.EqualsTester;

public class PathTest {

  private Path path;

  @Before
  public void setUp() {
    path = new Path();
  }

  @Test
  public void initialValues() {
    assertThat( path.getAll() ).isEmpty();
  }

  @Test
  public void addOrReplace() {
    path.addOrReplace( createZone( ZONE_1 ) );
    Zone expected = createZone( ZONE_1 );

    path.addOrReplace( expected );
    Collection<Zone> actual = path.getAll();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( zone -> zone == expected );
  }

  @Test
  public void getAll() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    Collection<Zone> actual = path.getAll();

    assertThat( actual )
      .hasSize( 2 )
      .contains( createZone( ZONE_1 ), createZone( ZONE_2 ) );
  }

  @Test
  public void find() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    Collection<Zone> actual = path.find( zone -> zone.getZoneEntity() == ZONE_1 );

    assertThat( actual )
    .hasSize( 1 )
    .contains( createZone( ZONE_1 ) );
  }

  @Test
  public void findWithNonMatchingPredicate() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    Collection<Zone> actual = path.find( zone -> zone.getZoneEntity() == ZONE_3 );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void findZoneActivation() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    Collection<Zone> actual = path.findZoneActivation( ZONE_1 );

    assertThat( actual )
      .hasSize( 1 )
      .contains( createZone( ZONE_1 ) );
  }

  @Test
  public void findZoneActivationIfNotContained() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    Collection<Zone> actual = path.findZoneActivation( ZONE_3 );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void findInPathRelease() {
    path.addOrReplace( createInPathReleasedZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    Collection<Zone> actual = path.findInPathReleases();

    assertThat( actual )
      .hasSize( 1 )
      .contains( createZone( ZONE_1 ) );
  }

  @Test
  public void findInPathReleaseIfNonExists() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    Collection<Zone> actual = path.findInPathReleases();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void remove() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    boolean actual = path.remove( asSet( createZone( ZONE_1 ) ) );

    assertThat( actual ).isTrue();
    assertThat( path.getAll() )
      .hasSize( 1 )
      .contains( createZone( ZONE_2 ) );
  }

  @Test
  public void removeOfNonMatching() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    boolean actual = path.remove( asSet( createZone( ZONE_3 ) ) );

    assertThat( actual ).isFalse();
    assertThat( path.getAll() )
      .hasSize( 2 )
      .contains( createZone( ZONE_2 ), createZone( ZONE_1 ) );
  }

  @Test
  public void isExpired() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isExpiredIfMultipleReleaseZoneActivationsExist() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );
    path.addOrReplace( createReleasedZone( ZONE_2 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void isExpiredIfAdditionalZoneActivationsExist() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void isExpiredIfReleasedZoneActivationIsNotExpired() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT - 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void isExpiredIfZoneActivationIsNotReleased() {
    path.addOrReplace( createZone( ZONE_1 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void getLatestReleaseTime() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );
    waitALittle();
    path.addOrReplace( createReleasedZone( ZONE_2 ) );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime )
      .isPresent()
      .isEqualTo( findReleaseTimeOf( ZONE_2 ) );
  }

  @Test
  public void getLatestReleaseTimeOnReverseAddition() {
    Zone zone1 = createReleasedZone( ZONE_1 );
    waitALittle();
    Zone zone2 = createReleasedZone( ZONE_2 );
    path.addOrReplace( zone2 );
    path.addOrReplace( zone1 );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime )
      .isPresent()
      .isEqualTo( findReleaseTimeOf( ZONE_2 ) );
  }

  @Test
  public void getLatestReleaseTimeIfTimestampIsEqual() {
    path.addOrReplace( createReleasedZone( ZONE_2 ) );
    path.addOrReplace( createReleasedZone( ZONE_1 ) );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime ).isPresent();
  }

  @Test
  public void getLatestReleaseTimeOfSingleElementPath() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime )
      .isPresent()
      .isEqualTo( findReleaseTimeOf( ZONE_1 ) );
  }

  @Test
  public void getLatestReleaseTimeOfPathWithoutReleasedActivations() {
    path.addOrReplace( createZone( ZONE_1 ) );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime ).isEmpty();
  }

  @Test
  public void getLatestReleaseTimeOfEmptyPath() {
    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime ).isEmpty();
  }

  @Test
  public void equalsAndHashCode() {
    EqualsTester<Path> tester = newInstance( path );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new Path(), new Path() );
    tester.assertEqual( createPath( createZone( ZONE_1 ) ), createPath( createZone( ZONE_1 ) ) );
    Zone releasedZone = createReleasedZone( ZONE_1 );
    tester.assertEqual( createPath( releasedZone ), createPath( releasedZone ) );
    tester.assertEqual( createPath( createInPathReleasedZone( ZONE_1 ) ),
                        createPath( createZone( ZONE_1 ) ) );
    tester.assertNotEqual( createPath( createZone( ZONE_1 ) ),
                           createPath( createZone( ZONE_2 ) ) );
    tester.assertNotEqual( createPath( createReleasedZone( ZONE_1 ) ),
                           createPath( createZone( ZONE_1 ) ) );
  }

  private static Path createPath( Zone zone ) {
    Path result = new Path();
    result.addOrReplace( zone );
    return result;
  }

  private Optional<LocalDateTime> findReleaseTimeOf( Entity<EntityDefinition<?>> zone ) {
    return path.findZoneActivation( zone ).iterator().next().getReleaseTime();
  }
}