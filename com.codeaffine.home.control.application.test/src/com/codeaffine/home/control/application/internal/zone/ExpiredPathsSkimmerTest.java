package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.ZoneActivationProviderImpl.PATH_EXPIRED_TIMEOUT;
import static com.codeaffine.home.control.application.test.ZoneActivationHelper.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.ZoneActivation;

public class ExpiredPathsSkimmerTest {

  private ExpiredPathsSkimmer skimmer;
  private Set<Path> paths;

  @Before
  public void setUp() {
    paths = new HashSet<>();
    skimmer = new ExpiredPathsSkimmer( paths );
  }

  @Test
  public void execute() {
    addOrReplaceInNewPath( createReleasedZoneActivation( ZONE_3 ) );
    Path survivor = addOrReplaceInNewPath( createZoneActivation( ZONE_1 ) );
    paths.forEach( path -> path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) ) );

    skimmer.execute();

    assertThat( paths )
      .hasSize( 1 )
      .contains( survivor );
    assertThat( survivor.getAll() )
      .hasSize( 1 )
      .contains( createZoneActivation( ZONE_1 ) );
  }

  @Test
  public void executeIfAllPathsAreExpired() {
    addOrReplaceInNewPath( createReleasedZoneActivation( ZONE_3 ) );
    TimeoutHelper.waitALittle();
    ZoneActivation expectedSurvivorZone = createReleasedZoneActivation( ZONE_1 );
    Path survivor = addOrReplaceInNewPath( expectedSurvivorZone );
    paths.forEach( path -> path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) ) );

    skimmer.execute();

    assertThat( paths )
      .hasSize( 1 )
      .contains( survivor );
    assertThat( survivor.getAll() )
      .hasSize( 1 )
      .contains( expectedSurvivorZone );
  }

  @Test
  public void executeIfAllPathsAreExpiredAtSameTime() {
    ZoneActivation zone1 = createReleasedZoneActivation( ZONE_1 );
    ZoneActivation zone2 = createReleasedZoneActivation( ZONE_2 );
    Path path1 = addOrReplaceInNewPath( zone1 );
    Path path2 = addOrReplaceInNewPath( zone2 );
    paths.forEach( path -> path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) ) );

    skimmer.execute();

    assertThat( paths )
      .hasSize( 1 )
      .allMatch( path -> path.equals( path1 ) || path.equals( path2 ) );
  }

  @Test
  public void executeIfAllPathsAreExpiredInReversedOrder() {
    ZoneActivation zone3Activation = createReleasedZoneActivation( ZONE_3 );
    TimeoutHelper.waitALittle();
    ZoneActivation expectedSurvivorZone = createReleasedZoneActivation( ZONE_1 );
    Path survivor = addOrReplaceInNewPath( expectedSurvivorZone );
    addOrReplaceInNewPath( zone3Activation );
    paths.forEach( path -> path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) ) );

    skimmer.execute();

    assertThat( paths )
      .hasSize( 1 )
      .contains( survivor );
    assertThat( survivor.getAll() )
      .hasSize( 1 )
      .contains( expectedSurvivorZone );
  }

  @Test
  public void executeIfOnlyOneExpiredPathExists() {
    ZoneActivation expectedSurvivorZone = createReleasedZoneActivation( ZONE_1 );
    Path survivor = addOrReplaceInNewPath( expectedSurvivorZone );
    paths.forEach( path -> path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) ) );

    skimmer.execute();

    assertThat( paths )
      .hasSize( 1 )
      .contains( survivor );
    assertThat( survivor.getAll() )
      .hasSize( 1 )
      .contains( expectedSurvivorZone );
  }

  private Path addOrReplaceInNewPath( ZoneActivation ... activations ) {
    Path result = new Path();
    Stream.of( activations ).forEach( activation  -> result.addOrReplace( activation ) );
    paths.add( result );
    return result;
  }
}