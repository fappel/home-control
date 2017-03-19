package com.codeaffine.home.control.application.internal.zone;

import java.util.Set;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class PathAdjacency {

  private final AdjacencyDefinition adjacencyDefinition;
  private final Set<Path> paths;

  PathAdjacency( AdjacencyDefinition adjacencyDefinition, Set<Path> paths ) {
    this.adjacencyDefinition = adjacencyDefinition;
    this.paths = paths;
  }

  boolean isAdjacentActivated( Entity<?> zone ) {
    return paths
      .stream()
      .flatMap( path -> path.getAll().stream() )
      .anyMatch( activation -> isAdjacent( activation.getZone(), zone ) );
  }

  boolean isAdjacentTo( Entity<?> zone, Set<ZoneActivation> range ) {
    return range.stream().anyMatch( activation -> isAdjacent( activation.getZone(), zone ) );
  }

  boolean isAdjacentToMoreThanOneActivation( Entity<?> zone, Path path ) {
    return path.find( activation -> isAdjacent( activation.getZone(), zone ) ).size() > 1;
  }

  boolean isRelated( Entity<?> zone, Path path ) {
    if( pathsContainsNewOrZoneMatchesSingleElementPath( zone ) ) {
      return isRelatedToActivatedZones( zone, path );
    }
    return !path.find( activation -> isAdjacentOrActual( zone, activation ) ).isEmpty();
  }

  boolean isRelatedToActivatedZones( Entity<?> zone, Path path ) {
    return !path.find( activation -> isNonReleasedAdjacentOrActual( zone, activation ) ).isEmpty();
  }

  private boolean pathsContainsNewOrZoneMatchesSingleElementPath( Entity<?> zone ) {
    return paths.stream().anyMatch( path -> isNewPathOrZoneMatchesSingleElementPath( zone, path ) );
  }

  private static boolean isNewPathOrZoneMatchesSingleElementPath( Entity<?> zone, Path path ) {
    return path.isEmpty() || path.size() == 1 && !path.findZoneActivation( zone ).isEmpty();
  }

  private boolean isNonReleasedAdjacentOrActual( Entity<?> zone, ZoneActivation activation ) {
    return isNonReleasedAdjacent( zone, activation ) || isActual( zone, activation );
  }

  private boolean isNonReleasedAdjacent( Entity<?> zone, ZoneActivation activation ) {
    return !activation.getReleaseTime().isPresent() && isAdjacent( zone, activation.getZone() );
  }

  private boolean isAdjacentOrActual( Entity<?> zone, ZoneActivation activation ) {
    return isAdjacent( activation.getZone(), zone ) || isActual( zone, activation );
  }

  private static boolean isActual( Entity<?> zone, ZoneActivation activation ) {
    return zone == activation.getZone();
  }

  private boolean isAdjacent( Entity<?> zone1, Entity<?> zone2 ) {
    EntityDefinition<?> zoneDefinition1 = ( EntityDefinition<?> )zone1.getDefinition();
    EntityDefinition<?> zoneDefinition2 = ( EntityDefinition<?> )zone2.getDefinition();
    return adjacencyDefinition.isAdjacent( zoneDefinition1, zoneDefinition2 );
  }
}