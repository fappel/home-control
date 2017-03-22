package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.ActivationProviderImpl.IN_PATH_RELEASES_EXPIRATION_TIME;
import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.status.Activation.Zone;

public class ExpiredInPathReleaseSkimmerTest {

  private Set<Path> paths;
  private ExpiredInPathReleaseSkimmer skimmer;

  @Before
  public void setUp() {
    paths = new HashSet<>();
    skimmer = new ExpiredInPathReleaseSkimmer( paths );
  }

  @Test
  public void execute() {
    ZoneImpl expected = createZone( ZONE_1 );
    addOrReplaceInNewPath( expected, createInPathReleasedZone( ZONE_2 ) );
    addOrReplaceInNewPath( createInPathReleasedZone( ZONE_3 ) );
    skimmer.setTimeSupplier( () -> now().plusSeconds( IN_PATH_RELEASES_EXPIRATION_TIME + 1 ) );
    Consumer<Zone> rebuilder = newRebuilderSpy();

    skimmer.execute( rebuilder );

    assertThat( captureZoneForRepopulation( rebuilder ) ).isSameAs( expected );
    assertThat( paths.isEmpty() );
  }

  @Test
  public void executeWithoutExpiredInPathRelease() {
    addOrReplaceInNewPath( createZone( ZONE_1 ), createInPathReleasedZone( ZONE_2 ) );
    addOrReplaceInNewPath( createInPathReleasedZone( ZONE_3 ) );
    skimmer.setTimeSupplier( () -> now() );
    Consumer<Zone> rebuilder = newRebuilderSpy();

    skimmer.execute( rebuilder );

    verify( rebuilder, never() ).accept( any( Zone.class ) );
    assertThat( paths ).hasSize( 2 );
  }

  private static Zone captureZoneForRepopulation( Consumer<Zone> rebuilder ) {
    ArgumentCaptor<Zone> captor = forClass( Zone.class );
    verify( rebuilder ).accept( captor.capture() );
    return captor.getValue();
  }

  @SuppressWarnings("unchecked")
  private static Consumer<Zone> newRebuilderSpy() {
    return mock( Consumer.class );
  }

  private Path addOrReplaceInNewPath( Zone ... zonesa ) {
    Path result = new Path();
    Stream.of( zonesa ).forEach( zone  -> result.addOrReplace( zone ) );
    paths.add( result );
    return result;
  }
}