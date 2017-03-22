package com.codeaffine.home.control.application.internal.activation;

import static com.codeaffine.home.control.application.internal.activation.Messages.RELEASED_TAG;
import static java.util.stream.Collectors.joining;

import com.codeaffine.home.control.application.status.Activation.Zone;

class PathLogEntry {

  private final Path path;

  PathLogEntry( Path path ) {
    this.path = path;
  }

  @Override
  public String toString() {
    return createListOfZonesInPath( path );
  }

  private static String createListOfZonesInPath( Path path ) {
    return path
      .getAll()
      .stream()
      .map( zone -> getActivationAsString( zone ) ).collect( joining( ", " ) );
  }

  private static String getActivationAsString( Zone zone ) {
    StringBuilder result = new StringBuilder( zone.getZoneEntity().getDefinition().toString() );
    zone.getReleaseTime().ifPresent( time -> appendReleasedTag( result ) );
    return result.toString();
  }

  private static StringBuilder appendReleasedTag( StringBuilder result ) {
    return result.append( " <" ).append( RELEASED_TAG ).append( ">" );
  }
}