package com.codeaffine.home.control.status.internal.activation;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.status.internal.activation.Path;
import com.codeaffine.home.control.status.internal.activation.PathAdjacency;
import com.codeaffine.home.control.status.internal.activation.ZoneImpl;
import com.codeaffine.home.control.status.supplier.AdjacencyDefinition;
import com.codeaffine.home.control.status.supplier.Activation.Zone;

public class PathAdjacencyTest {

  private AdjacencyDefinition adjacency;
  private PathAdjacency pathAdjacency;
  private Set<Path> paths;

  @Before
  public void setUp() {
    adjacency = new AdjacencyDefinition( asSet( ZONE_DEFINITION_1, ZONE_DEFINITION_2, ZONE_DEFINITION_3 ) );
    adjacency.link( ZONE_DEFINITION_1, ZONE_DEFINITION_2 ).link( ZONE_DEFINITION_2, ZONE_DEFINITION_3 );
    paths = new HashSet<>();
    pathAdjacency = new PathAdjacency( adjacency, paths );
  }

  @Test
  public void getZonesOfRelatedPaths() {
    ZoneImpl lookupZone = createZone( ZONE_1 );
    addOrReplaceInNewPath( lookupZone, createZone( ZONE_2 ) );
    addOrReplaceInNewPath( createZone( ZONE_3 ) );

    Set<Zone> actual = pathAdjacency.getZonesOfRelatedPaths( lookupZone );

    assertThat( actual )
      .hasSize( 2 )
      .contains( lookupZone, createZone( ZONE_2 ) );
  }

  @Test
  public void getZonesOfRelatedPathsIfLookUpZoneBelongsToMultiplePaths() {
    ZoneImpl lookupZone = createZone( ZONE_1 );
    addOrReplaceInNewPath( lookupZone, createZone( ZONE_2 ) );
    addOrReplaceInNewPath( lookupZone, createZone( ZONE_3 ) );

    Set<Zone> actual = pathAdjacency.getZonesOfRelatedPaths( lookupZone );

    assertThat( actual )
      .hasSize( 3 )
      .contains( lookupZone, createZone( ZONE_2 ), createZone( ZONE_3 ) );
  }

  @Test
  public void isAdjacentActivated() {
    addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentActivated( ZONE_2 );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentActivatedWithNonAdjacentZone() {
    addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentActivated( ZONE_3 );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentActivatedOnSelfReflection() {
    addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentActivated( ZONE_1 );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentActivatedOnEmptyPathsStructure() {
    boolean actual = pathAdjacency.isAdjacentActivated( ZONE_2 );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentTo() {
    Set<Zone> inPathReleases = asSet( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentTo( ZONE_2, inPathReleases );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentToOnContainment() {
    Set<Zone> inPathReleases = asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) );

    boolean actual = pathAdjacency.isAdjacentTo( ZONE_2, inPathReleases );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentToWithNonAdjacentZone() {
    Set<Zone> inPathReleases = asSet( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentTo( ZONE_3, inPathReleases );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToOnSelfReflection() {
    Set<Zone> inPathReleases = asSet( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentTo( ZONE_1, inPathReleases );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToOnEmptyInPathReleaseSet() {
    boolean actual = pathAdjacency.isAdjacentTo( ZONE_1, asSet() );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToMoreThanOneActivation() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ), createZone( ZONE_3 ) );

    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_2, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentToMoreThanOneActivationIfAdjacentToEndOfPath() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_2 ), createZone( ZONE_3 ) );

    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_1, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToMoreThanOneActivationIfIsEndOfPath() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_2 ), createZone( ZONE_3 ) );

    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_2, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToMoreThanOneActivationIfPathHasOnlyOneElement() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_3 ) );

    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_2, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToMoreThanOneActivationIfPathIsEmpty() {
    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_2, new Path() );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelated() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_2, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedOnContainment() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedOnNonAdjacent() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_3, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedOnReleased() {
    Path path = addOrReplaceInNewPath( createReleasedZone( ZONE_1 ), createReleasedZone( ZONE_2 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_3, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedOnReleasedButNewPathAwaitsInsert() {
    Path path = addOrReplaceInNewPath( createReleasedZone( ZONE_1 ), createReleasedZone( ZONE_2 ) );
    paths.add( new Path() );

    boolean actual = pathAdjacency.isRelated( ZONE_3, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedOnReleasedButHasSelfContainedPath() {
    Path path = addOrReplaceInNewPath( createReleasedZone( ZONE_1 ) );
    addOrReplaceInNewPath( createZone( ZONE_2 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_2, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedOnSelfContainement() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ), createZone( ZONE_2 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedToActivatedZones() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_2, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedToActivatedZonesOnContainment() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedToActivatedZonesIfNotAdjacent() {
    Path path = addOrReplaceInNewPath( createZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_3, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedToActivatedZonesIfReleased() {
    Path path = addOrReplaceInNewPath( createReleasedZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_2, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedToActivatedZonesIfReleasedButContained() {
    Path path = addOrReplaceInNewPath( createReleasedZone( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  private ZoneImpl createReleasedZone( Entity<EntityDefinition<?>> zone ) {
    return createZone( zone ).markAsReleased();
  }

  private ZoneImpl createZone( Entity<EntityDefinition<?>> zone ) {
    return new ZoneImpl( zone, pathAdjacency );
  }

  private Path addOrReplaceInNewPath( ZoneImpl ... activations ) {
    Path result = new Path();
    Stream.of( activations ).forEach( activation  -> result.addOrReplace( activation ) );
    paths.add( result );
    return result;
  }
}