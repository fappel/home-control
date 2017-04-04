package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.test.ActivationHelper.createZone;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.internal.activation.PathAdjacency;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;

class AnalysisTestsDoubleHelper {

  private final ActivationProvider activationProvider;
  private final ActivityProvider activityProvider;
  private final ActivityMath activityMath;
  private final PathAdjacency adjacency;

  AnalysisTestsDoubleHelper() {
    adjacency = mock( PathAdjacency.class );
    activationProvider = mock( ActivationProvider.class );
    activityProvider = mock( ActivityProvider.class );
    activityMath = new ActivityMath( activityProvider, activationProvider );
  }

  PathAdjacency getAdjacency() {
    return adjacency;
  }

  ActivationProvider getActivationProvider() {
    return activationProvider;
  }

  ActivityProvider getActivityProvider() {
    return activityProvider;
  }

  ActivityMath getActivityMath() {
    return activityMath;
  }

  void stubActivityProvider( Activity activity ) {
    when( activityProvider.getStatus() ).thenReturn( activity );
  }

  Set<Zone> createZones( SectionDefinition ... sectionDefinitions  ) {
    return Stream.of( sectionDefinitions )
      .map( sectionDefinition -> createZone( stubEntity( sectionDefinition ), adjacency ) )
      .collect( toSet() );
  }

  void stubAdjacency( SectionDefinition sectionDefinition, Set<Zone> zones ) {
    Predicate<Zone> filter = zone -> zone.getZoneEntity().getDefinition().equals( sectionDefinition );
    Zone lookup = zones.stream().filter( filter ).findAny().get();
    when( adjacency.getZonesOfRelatedPaths( lookup ) ).thenReturn( zones );
    when( adjacency.isAdjacentActivated( lookup.getZoneEntity() ) ).thenReturn( zones.size() > 1 );
  }

  void stubActivationProvider( Set<Zone> zones ) {
    when( activationProvider.getStatus() ).thenReturn( new Activation( zones ) );
  }

  static List<Object> $( SectionDefinition sectionDefinition, Percent sectionActivity ) {
    return asList( sectionDefinition, sectionActivity );
  }

  @SafeVarargs
  static Activity newActivity( Percent overallActivity, List<Object> ... activationEntries ) {
    Map<SectionDefinition, Percent> activations = collectActivations( activationEntries );
    return new Activity( overallActivity, activations, activations );
  }

  @SafeVarargs
  private static Map<SectionDefinition, Percent> collectActivations( List<Object>... activationEntries ) {
    return Stream.of( activationEntries )
        .collect( toMap( activationEntry -> ( SectionDefinition )activationEntry.get( 0 ),
                         activationEntry -> ( Percent )activationEntry.get( 1 ) ) );
  }
}