package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.TimeoutHelper.waitALittle;
import static com.codeaffine.home.control.application.internal.zone.ZoneActivationProviderImpl.PATH_EXPIRED_TIMEOUT;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.test.util.lang.EqualsTester;

public class PathTest {

  private static final EntityDefinition<?> ZONE_DEFINITION_1 = stubEntityDefinition( "Zone1" );
  private static final EntityDefinition<?> ZONE_DEFINITION_2 = stubEntityDefinition( "Zone2" );
  private static final EntityDefinition<?> ZONE_DEFINITION_3 = stubEntityDefinition( "Zone3" );
  private static final Entity<EntityDefinition<?>> ZONE_1 = stubEntity( ZONE_DEFINITION_1 );
  private static final Entity<EntityDefinition<?>> ZONE_2 = stubEntity( ZONE_DEFINITION_2 );
  private static final Entity<EntityDefinition<?>> ZONE_3 = stubEntity( ZONE_DEFINITION_3 );

  private Path path;

  @Before
  public void setUp() {
    path = new Path();
  }

  @Test
  public void initialValues() {
    assertThat( path.isEmpty() ).isTrue();
    assertThat( path.size() ).isEqualTo( 0 );
  }

  @Test
  public void isEmpty() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );

    boolean actual = path.isEmpty();

    assertThat( actual ).isFalse();
  }

  @Test
  public void size() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );

    int actual = path.size();

    assertThat( actual ).isEqualTo( 1 );
  }

  @Test
  public void addOrReplace() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    ZoneActivation expected = createZoneActivation( ZONE_1 );

    path.addOrReplace( expected );
    Collection<ZoneActivation> actual = path.getAll();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( activation -> activation == expected );
  }

  @Test
  public void getAll() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    Collection<ZoneActivation> actual = path.getAll();

    assertThat( actual )
      .hasSize( 2 )
      .contains( createZoneActivation( ZONE_1 ), createZoneActivation( ZONE_2 ) );
  }

  @Test
  public void find() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    Collection<ZoneActivation> actual = path.find( activation -> activation.getZone() == ZONE_1 );

    assertThat( actual )
    .hasSize( 1 )
    .contains( createZoneActivation( ZONE_1 ) );
  }

  @Test
  public void findWithNonMatchingPredicate() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    Collection<ZoneActivation> actual = path.find( activation -> activation.getZone() == ZONE_3 );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void findZoneActivation() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    Collection<ZoneActivation> actual = path.findZoneActivation( ZONE_1 );

    assertThat( actual )
      .hasSize( 1 )
      .contains( createZoneActivation( ZONE_1 ) );
  }

  @Test
  public void findZoneActivationIfNotContained() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    Collection<ZoneActivation> actual = path.findZoneActivation( ZONE_3 );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void findInPathRelease() {
    path.addOrReplace( createInPathReleasedZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    Collection<ZoneActivation> actual = path.findInPathReleases();

    assertThat( actual )
      .hasSize( 1 )
      .contains( createZoneActivation( ZONE_1 ) );
  }

  @Test
  public void findInPathReleaseIfNonExists() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    Collection<ZoneActivation> actual = path.findInPathReleases();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void remove() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    boolean actual = path.remove( asSet( createZoneActivation( ZONE_1 ) ) );

    assertThat( actual ).isTrue();
    assertThat( path.getAll() )
      .hasSize( 1 )
      .contains( createZoneActivation( ZONE_2 ) );
  }

  @Test
  public void removeOfNonMatching() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );

    boolean actual = path.remove( asSet( createZoneActivation( ZONE_3 ) ) );

    assertThat( actual ).isFalse();
    assertThat( path.getAll() )
      .hasSize( 2 )
      .contains( createZoneActivation( ZONE_2 ), createZoneActivation( ZONE_1 ) );
  }

  @Test
  public void isExpired() {
    path.addOrReplace( createReleasedZoneActivation( ZONE_1 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isExpiredIfMultipleReleaseZoneActivationsExist() {
    path.addOrReplace( createReleasedZoneActivation( ZONE_1 ) );
    path.addOrReplace( createReleasedZoneActivation( ZONE_2 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void isExpiredIfAdditionalZoneActivationsExist() {
    path.addOrReplace( createReleasedZoneActivation( ZONE_1 ) );
    path.addOrReplace( createZoneActivation( ZONE_2 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void isExpiredIfReleasedZoneActivationIsNotExpired() {
    path.addOrReplace( createReleasedZoneActivation( ZONE_1 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT - 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void isExpiredIfZoneActivationIsNotReleased() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );
    path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) );

    boolean actual = path.isExpired();

    assertThat( actual ).isFalse();
  }

  @Test
  public void getLatestReleaseTime() {
    path.addOrReplace( createReleasedZoneActivation( ZONE_1 ) );
    waitALittle();
    path.addOrReplace( createReleasedZoneActivation( ZONE_2 ) );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime )
      .isPresent()
      .isEqualTo( findReleaseTimeOf( ZONE_2 ) );
  }

  @Test
  public void getLatestReleaseTimeOnReverseAddition() {
    ZoneActivation activation1 = createReleasedZoneActivation( ZONE_1 );
    waitALittle();
    ZoneActivation activation2 = createReleasedZoneActivation( ZONE_2 );
    path.addOrReplace( activation2 );
    path.addOrReplace( activation1 );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime )
      .isPresent()
      .isEqualTo( findReleaseTimeOf( ZONE_2 ) );
  }

  @Test
  public void getLatestReleaseTimeIfTimestampIsEqual() {
    path.addOrReplace( createReleasedZoneActivation( ZONE_2 ) );
    path.addOrReplace( createReleasedZoneActivation( ZONE_1 ) );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime ).isPresent();
  }

  @Test
  public void getLatestReleaseTimeOfSingleElementPath() {
    path.addOrReplace( createReleasedZoneActivation( ZONE_1 ) );

    Optional<LocalDateTime> latestReleaseTime = path.getLatestReleaseTime();

    assertThat( latestReleaseTime )
      .isPresent()
      .isEqualTo( findReleaseTimeOf( ZONE_1 ) );
  }

  @Test
  public void getLatestReleaseTimeOfPathWithoutReleasedActivations() {
    path.addOrReplace( createZoneActivation( ZONE_1 ) );

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
    EqualsTester<Path> tester = EqualsTester.newInstance( path );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new Path(), new Path() );
    tester.assertEqual( createPath( createZoneActivation( ZONE_1 ) ), createPath( createZoneActivation( ZONE_1 ) ) );
    tester.assertEqual( createPath( createReleasedZoneActivation( ZONE_1 ) ),
                        createPath( createReleasedZoneActivation( ZONE_1 ) ) );
    tester.assertEqual( createPath( createInPathReleasedZoneActivation( ZONE_1 ) ),
                        createPath( createZoneActivation( ZONE_1 ) ) );
    tester.assertNotEqual( createPath( createZoneActivation( ZONE_1 ) ),
                           createPath( createZoneActivation( ZONE_2 ) ) );
    tester.assertNotEqual( createPath( createReleasedZoneActivation( ZONE_1 ) ),
                           createPath( createZoneActivation( ZONE_1 ) ) );
  }

  private static Path createPath( ZoneActivation activation ) {
    Path result = new Path();
    result.addOrReplace( activation );
    return result;
  }

  private Optional<LocalDateTime> findReleaseTimeOf( Entity<EntityDefinition<?>> zone ) {
    return path.findZoneActivation( zone ).iterator().next().getReleaseTime();
  }

  private static ZoneActivation createReleasedZoneActivation( Entity<?> zone ) {
    ZoneActivationImpl result = createZoneActivation( zone );
    result.markAsReleased();
    return result;
  }

  private static ZoneActivation createInPathReleasedZoneActivation( Entity<?> zone ) {
    ZoneActivationImpl result = createZoneActivation( zone );
    result.markForInPathRelease();
    return result;
  }

  private static ZoneActivationImpl createZoneActivation( Entity<?> zone ) {
    return new ZoneActivationImpl( zone, mock( PathAdjacency.class ) );
  }
}