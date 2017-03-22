package com.codeaffine.home.control.application.internal.activation;

import static com.codeaffine.home.control.application.internal.activation.ActivationProviderImpl.PATH_EXPIRED_TIMEOUT;
import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.internal.activation.ExpiredPathsSkimmer;
import com.codeaffine.home.control.application.internal.activation.Path;
import com.codeaffine.home.control.application.status.Activation.Zone;

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
    addOrReplaceInNewPath( createReleasedZone( ZONE_3 ) );
    Path survivor = addOrReplaceInNewPath( createZone( ZONE_1 ) );
    paths.forEach( path -> path.setTimeSupplier( () -> now().plusSeconds( PATH_EXPIRED_TIMEOUT + 1 ) ) );

    skimmer.execute();

    assertThat( paths )
      .hasSize( 1 )
      .contains( survivor );
    assertThat( survivor.getAll() )
      .hasSize( 1 )
      .contains( createZone( ZONE_1 ) );
  }

  @Test
  public void executeIfAllPathsAreExpired() {
    addOrReplaceInNewPath( createReleasedZone( ZONE_3 ) );
    TimeoutHelper.waitALittle();
    Zone expectedSurvivorZone = createReleasedZone( ZONE_1 );
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
    Zone zone1 = createReleasedZone( ZONE_1 );
    Zone zone2 = createReleasedZone( ZONE_2 );
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
    Zone zone3Activation = createReleasedZone( ZONE_3 );
    TimeoutHelper.waitALittle();
    Zone expectedSurvivorZone = createReleasedZone( ZONE_1 );
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
    Zone expectedSurvivorZone = createReleasedZone( ZONE_1 );
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

  private Path addOrReplaceInNewPath( Zone ... zones ) {
    Path result = new Path();
    Stream.of( zones ).forEach( zone  -> result.addOrReplace( zone ) );
    paths.add( result );
    return result;
  }
}