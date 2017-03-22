package com.codeaffine.home.control.application.internal.activation;

import java.util.Set;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class PathAdjacency {

  private final AdjacencyDefinition adjacencyDefinition;
  private final Set<Path> paths;

  PathAdjacency( AdjacencyDefinition adjacencyDefinition, Set<Path> paths ) {
    this.adjacencyDefinition = adjacencyDefinition;
    this.paths = paths;
  }

  boolean isAdjacentActivated( Entity<?> zoneEntity ) {
    return paths
      .stream()
      .flatMap( path -> path.getAll().stream() )
      .anyMatch( zone -> isAdjacent( zone.getZoneEntity(), zoneEntity ) );
  }

  boolean isAdjacentTo( Entity<?> zoneEntity, Set<Zone> range ) {
    return range.stream().anyMatch( zone -> isAdjacent( zone.getZoneEntity(), zoneEntity ) );
  }

  boolean isAdjacentToMoreThanOneActivation( Entity<?> zoneEntity, Path path ) {
    return path.find( zone -> isAdjacent( zone.getZoneEntity(), zoneEntity ) ).size() > 1;
  }

  boolean isRelated( Entity<?> zoneEntity, Path path ) {
    if( pathsContainsNewOrZoneMatchesSingleElementPath( zoneEntity ) ) {
      return isRelatedToActivatedZones( zoneEntity, path );
    }
    return !path.find( zone -> isAdjacentOrActual( zoneEntity, zone ) ).isEmpty();
  }

  boolean isRelatedToActivatedZones( Entity<?> zoneEntity, Path path ) {
    return !path.find( zone -> isNonReleasedAdjacentOrActual( zoneEntity, zone ) ).isEmpty();
  }

  private boolean pathsContainsNewOrZoneMatchesSingleElementPath( Entity<?> zoneEntity ) {
    return paths.stream().anyMatch( path -> isNewPathOrZoneMatchesSingleElementPath( zoneEntity, path ) );
  }

  private static boolean isNewPathOrZoneMatchesSingleElementPath( Entity<?> zoneEntity, Path path ) {
    return path.getAll().isEmpty() || path.getAll().size() == 1 && !path.findZoneActivation( zoneEntity ).isEmpty();
  }

  private boolean isNonReleasedAdjacentOrActual( Entity<?> zoneEntity, Zone zone ) {
    return isNonReleasedAdjacent( zoneEntity, zone ) || isActual( zoneEntity, zone );
  }

  private boolean isNonReleasedAdjacent( Entity<?> zoneEntity, Zone zone ) {
    return !zone.getReleaseTime().isPresent() && isAdjacent( zoneEntity, zone.getZoneEntity() );
  }

  private boolean isAdjacentOrActual( Entity<?> zoneEntity, Zone zone ) {
    return isAdjacent( zone.getZoneEntity(), zoneEntity ) || isActual( zoneEntity, zone );
  }

  private static boolean isActual( Entity<?> zoneEntity, Zone zone ) {
    return zoneEntity == zone.getZoneEntity();
  }

  private boolean isAdjacent( Entity<?> zoneEntity1, Entity<?> zoneEntity2 ) {
    EntityDefinition<?> zoneDefinition1 = ( EntityDefinition<?> )zoneEntity1.getDefinition();
    EntityDefinition<?> zoneDefinition2 = ( EntityDefinition<?> )zoneEntity2.getDefinition();
    return adjacencyDefinition.isAdjacent( zoneDefinition1, zoneDefinition2 );
  }
}