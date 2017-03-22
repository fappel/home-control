package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.ActivationProviderImpl.IN_PATH_RELEASES_EXPIRATION_TIME;
import static java.util.stream.Collectors.toSet;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.codeaffine.home.control.application.status.Activation.Zone;

class ExpiredInPathReleaseSkimmer {

  private final Set<Path> paths;

  private Supplier<LocalDateTime> timeSupplier;

  ExpiredInPathReleaseSkimmer( Set<Path> paths ) {
    this.paths = paths;
  }

  void setTimeSupplier( Supplier<LocalDateTime> timeSupplier ) {
    this.timeSupplier = timeSupplier;
  }

  void execute( Consumer<Zone> rebuilder ) {
    Set<Zone> expiredInPathReleases = collectExpiredInPathReleases();
    if( !expiredInPathReleases.isEmpty() ) {
      paths.stream().forEach( path -> path.remove( expiredInPathReleases ) );
      Set<Path> clone = new HashSet<>( paths );
      paths.clear();
      clone.stream().forEach( path -> populate( rebuilder, path ) );
    }
  }

  private Set<Zone> collectExpiredInPathReleases() {
    return paths
      .stream()
      .flatMap( path -> collectExpiredInPathReleases( path ).stream() )
      .collect( toSet() );
  }

  private Set<Zone> collectExpiredInPathReleases( Path path ) {
    return path.findInPathReleases().stream().filter( zone -> isExpiredInPathRelease( zone ) ).collect( toSet() );
  }

  private boolean isExpiredInPathRelease( Zone zone ) {
    LocalDateTime inPathReleaseMark = ( ( ZoneImpl )zone ).getInPathReleaseMarkTime().get();
    return inPathReleaseMark.plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME ).isBefore( timeSupplier.get() );
  }

  private static void populate( Consumer<Zone> rebuilder, Path path ) {
    path.getAll()
      .stream()
      .collect( toSet() )
      .forEach( rebuilder );
  }
}