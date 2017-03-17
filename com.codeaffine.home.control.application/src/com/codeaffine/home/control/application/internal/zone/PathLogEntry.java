package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.Messages.RELEASED_TAG;
import static java.util.stream.Collectors.joining;

import com.codeaffine.home.control.application.status.ZoneActivation;

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
      .map( activation -> getActivationAsString( activation ) ).collect( joining( ", " ) );
  }

  private static String getActivationAsString( ZoneActivation activation ) {
    StringBuilder result = new StringBuilder( activation.getZone().getDefinition().toString() );
    activation.getReleaseTime().ifPresent( time -> appendReleasedTag( result ) );
    return result.toString();
  }

  private static StringBuilder appendReleasedTag( StringBuilder result ) {
    return result.append( " <" ).append( RELEASED_TAG ).append( ">" );
  }
}