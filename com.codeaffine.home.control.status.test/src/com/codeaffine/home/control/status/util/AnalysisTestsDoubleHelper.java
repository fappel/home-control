package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.createZone;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.codeaffine.home.control.status.internal.activation.PathAdjacency;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.Activity;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.type.Percent;
import com.codeaffine.home.control.status.util.ActivityMath;

class AnalysisTestsDoubleHelper {

  private final ActivationSupplier activationSupplier;
  private final ActivitySupplier activitySupplier;
  private final ActivityMath activityMath;
  private final PathAdjacency adjacency;

  AnalysisTestsDoubleHelper() {
    adjacency = mock( PathAdjacency.class );
    activationSupplier = mock( ActivationSupplier.class );
    activitySupplier = mock( ActivitySupplier.class );
    activityMath = new ActivityMath( activitySupplier, activationSupplier );
  }

  PathAdjacency getAdjacency() {
    return adjacency;
  }

  ActivationSupplier getActivationSupplier() {
    return activationSupplier;
  }

  ActivitySupplier getActivitySupplier() {
    return activitySupplier;
  }

  ActivityMath getActivityMath() {
    return activityMath;
  }

  void stubActivitySupplier( Activity activity ) {
    when( activitySupplier.getStatus() ).thenReturn( activity );
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

  void stubActivationSupplier( Set<Zone> zones ) {
    when( activationSupplier.getStatus() ).thenReturn( new Activation( zones ) );
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