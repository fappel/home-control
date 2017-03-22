package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

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
  public void isAdjacentActivated() {
    addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentActivated( ZONE_2 );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentActivatedWithNonAdjacentZone() {
    addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentActivated( ZONE_3 );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentActivatedOnSelfReflection() {
    addOrReplaceInNewPath( createActivation( ZONE_1 ) );

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
    Set<Zone> inPathReleases = asSet( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentTo( ZONE_2, inPathReleases );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentToOnContainment() {
    Set<Zone> inPathReleases = asSet( createActivation( ZONE_1 ), createActivation( ZONE_2 ) );

    boolean actual = pathAdjacency.isAdjacentTo( ZONE_2, inPathReleases );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentToWithNonAdjacentZone() {
    Set<Zone> inPathReleases = asSet( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isAdjacentTo( ZONE_3, inPathReleases );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToOnSelfReflection() {
    Set<Zone> inPathReleases = asSet( createActivation( ZONE_1 ) );

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
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ), createActivation( ZONE_3 ) );

    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_2, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentToMoreThanOneActivationIfAdjacentToEndOfPath() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_2 ), createActivation( ZONE_3 ) );

    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_1, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToMoreThanOneActivationIfIsEndOfPath() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_2 ), createActivation( ZONE_3 ) );

    boolean actual = pathAdjacency.isAdjacentToMoreThanOneActivation( ZONE_2, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isAdjacentToMoreThanOneActivationIfPathHasOnlyOneElement() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_3 ) );

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
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_2, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedOnContainment() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedOnNonAdjacent() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_3, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedOnReleased() {
    Path path = addOrReplaceInNewPath( createReleasedActivation( ZONE_1 ), createReleasedActivation( ZONE_2 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_3, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedOnReleasedButNewPathAwaitsInsert() {
    Path path = addOrReplaceInNewPath( createReleasedActivation( ZONE_1 ), createReleasedActivation( ZONE_2 ) );
    paths.add( new Path() );

    boolean actual = pathAdjacency.isRelated( ZONE_3, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedOnReleasedButHasSelfContainedPath() {
    Path path = addOrReplaceInNewPath( createReleasedActivation( ZONE_1 ) );
    addOrReplaceInNewPath( createActivation( ZONE_2 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_2, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedOnSelfContainement() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ), createActivation( ZONE_2 ) );

    boolean actual = pathAdjacency.isRelated( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedToActivatedZones() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_2, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedToActivatedZonesOnContainment() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isRelatedToActivatedZonesIfNotAdjacent() {
    Path path = addOrReplaceInNewPath( createActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_3, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedToActivatedZonesIfReleased() {
    Path path = addOrReplaceInNewPath( createReleasedActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_2, path );

    assertThat( actual ).isFalse();
  }

  @Test
  public void isRelatedToActivatedZonesIfReleasedButContained() {
    Path path = addOrReplaceInNewPath( createReleasedActivation( ZONE_1 ) );

    boolean actual = pathAdjacency.isRelatedToActivatedZones( ZONE_1, path );

    assertThat( actual ).isTrue();
  }

  private ZoneImpl createReleasedActivation( Entity<EntityDefinition<?>> zone ) {
    return createActivation( zone ).markAsReleased();
  }

  private ZoneImpl createActivation( Entity<EntityDefinition<?>> zone ) {
    return new ZoneImpl( zone, pathAdjacency );
  }

  private Path addOrReplaceInNewPath( ZoneImpl ... activations ) {
    Path result = new Path();
    Stream.of( activations ).forEach( activation  -> result.addOrReplace( activation ) );
    paths.add( result );
    return result;
  }
}