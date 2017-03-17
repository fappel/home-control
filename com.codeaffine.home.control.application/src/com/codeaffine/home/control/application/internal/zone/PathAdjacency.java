package com.codeaffine.home.control.application.internal.zone;

import java.util.Set;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

class PathAdjacency {

  private final AdjacencyDefinition adjacencyDefinition;
  private final Set<Path> paths;

  PathAdjacency( AdjacencyDefinition adjacencyDefinition, Set<Path> paths ) {
    this.adjacencyDefinition = adjacencyDefinition;
    this.paths = paths;
  }
  boolean isAdjacentToInPathRelease( Entity<?> zone, Set<ZoneActivation> inPathReleases ) {
    return    inPathReleases.size() > 0
           && inPathReleases.stream().anyMatch( activation -> isAdjacent( activation.getZone(), zone ) );
  }

  boolean isInPathRelease( Entity<?> zone, Path path ) {
    return path.find( activation -> isAdjacent( activation.getZone(), zone ) ).size() > 1;
  }

  boolean belongsToPath( Entity<?> zone, Path path ) {
    if( hasNewPath() ) {
      return belongsToPathWithActivatedZones( zone, path );
    }
    return !path.find( activation -> isAdjacentOrActual( zone, activation ) ).isEmpty();
  }

  boolean belongsToPathWithActivatedZones( Entity<?> zone, Path path ) {
    return !path.find( activation -> isNonReleasedAdjacentOrActual( zone, activation ) ).isEmpty();
  }

  private boolean hasNewPath() {
    return paths.stream().anyMatch( path -> path.isEmpty() );
  }

  private boolean isNonReleasedAdjacentOrActual( Entity<?> zone, ZoneActivation activation ) {
    return !activation.getReleaseTime().isPresent() && isAdjacentOrActual( zone, activation );
  }

  private boolean isAdjacentOrActual( Entity<?> zone, ZoneActivation activation ) {
    return isAdjacent( activation.getZone(), zone ) || zone == activation.getZone();
  }

  private boolean isAdjacent( Entity<?> zone1, Entity<?> zone2 ) {
    EntityDefinition<?> zoneDefinition1 = ( EntityDefinition<?> )zone1.getDefinition();
    EntityDefinition<?> zoneDefinition2 = ( EntityDefinition<?> )zone2.getDefinition();
    return adjacencyDefinition.isAdjacent( zoneDefinition1, zoneDefinition2 );
  }
}