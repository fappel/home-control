package com.codeaffine.home.control.status.internal.activation;

import static java.util.stream.Collectors.toSet;

import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.status.supplier.AdjacencyDefinition;
import com.codeaffine.home.control.status.supplier.Activation.Zone;

public class PathAdjacency {

  private final AdjacencyDefinition adjacencyDefinition;
  private final Set<Path> paths;

  PathAdjacency( AdjacencyDefinition adjacencyDefinition, Set<Path> paths ) {
    this.adjacencyDefinition = adjacencyDefinition;
    this.paths = paths;
  }

  public Set<Zone> getZonesOfRelatedPaths( Zone zone ) {
    return paths
      .stream()
      .filter( path -> !path.findZoneActivation( zone.getZoneEntity() ).isEmpty() )
      .flatMap( path -> path.getAll().stream() )
      .collect( toSet() );
  }

  public boolean isAdjacentActivated( Entity<?> zoneEntity ) {
    return paths
      .stream()
      .flatMap( path -> path.getAll().stream() )
      .anyMatch( zone -> isAdjacent( zone.getZoneEntity(), zoneEntity ) );
  }

  public boolean isAdjacentTo( Entity<?> zoneEntity, Set<Zone> range ) {
    return range.stream().anyMatch( zone -> isAdjacent( zone.getZoneEntity(), zoneEntity ) );
  }

  public boolean isAdjacentToMoreThanOneActivation( Entity<?> zoneEntity, Path path ) {
    return path.find( zone -> isAdjacent( zone.getZoneEntity(), zoneEntity ) ).size() > 1;
  }

  public boolean isRelated( Entity<?> zoneEntity, Path path ) {
    if( pathsContainsNewOrZoneMatchesSingleElementPath( zoneEntity ) ) {
      return isRelatedToActivatedZones( zoneEntity, path );
    }
    return !path.find( zone -> isAdjacentOrActual( zoneEntity, zone ) ).isEmpty();
  }

  public boolean isRelatedToActivatedZones( Entity<?> zoneEntity, Path path ) {
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