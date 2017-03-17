package com.codeaffine.home.control.application.internal.zone;

import static java.util.stream.Collectors.toSet;

import java.util.Set;

class ExpiredPathsSkimmer {

  private final Set<Path> paths;

  ExpiredPathsSkimmer( Set<Path> paths ) {
    this.paths = paths;
  }

  void execute() {
    if( paths.size() > 1 ) {
      paths.removeAll( calculateRemovals() );
    }
  }

  private Set<Path> calculateRemovals() {
    Set<Path> result = paths.stream().filter( path -> path.isExpired() ).collect( toSet() );
    if( result.size() == paths.size() ) {
      Path last = result.stream().reduce( ( path1, path2 ) -> getMostRecentReleased( path1, path2 ) ).get();
      result.remove( last );
    }
    return result;
  }

  private static Path getMostRecentReleased( Path path1, Path path2 ) {
    if( path1.getLatestReleaseTime().get().isBefore( path2.getLatestReleaseTime().get() ) ) {
      return path2;
    }
    return path1;
  }
}