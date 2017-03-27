package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.ActivationHelper.createZone;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.internal.activation.PathAdjacency;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;

public class ActivityMathTest {

  private ActivationProvider activationProvider;
  private ActivityProvider activityProvider;
  private ActivityMath activityMath;
  private PathAdjacency adjacency;

  @Before
  public void setUp() {
    adjacency = mock( PathAdjacency.class );
    activationProvider = mock( ActivationProvider.class );
    activityProvider = mock( ActivityProvider.class );
    activityMath = new ActivityMath( activityProvider, activationProvider );
  }

  @Test
  public void calculateGeometricMeanOfPathActivityFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_003 );
  }

  @Test
  public void calculateGeometricMeanOfPathActivityForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateGeometricMeanOfPathActivityForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_005 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMaximumOfPathActivityFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathActivityForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathActivityForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMinimumOfPathActivityFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_001 );
  }

  @Test
  public void calculateMinimumOfPathActivityForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMinimumOfPathActivityForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void calculateGeometricMeanOfPathActivityForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateGeometricMeanOfPathActivityFor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void calculateArithmeticMeanOfPathActivityForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateArithmeticMeanOfPathActivityFor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void calculateMaximumOfPathActivityForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateMaximumOfPathActivityFor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void calculateMinimumOfPathActivityForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateMinimumOfPathActivityFor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivityProviderArgument() {
    new ActivityMath( null, activationProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationProviderArgument() {
    new ActivityMath( activityProvider, null );
  }

  @SafeVarargs
  private static Activity newActivity( Percent overallActivity, List<Object> ... sectionActivityEntries ) {
    Map<SectionDefinition, Percent> activities = new HashMap<>();
    Stream.of( sectionActivityEntries ).forEach( sectionActivityEntry -> {
      activities.put( ( SectionDefinition )sectionActivityEntry.get( 0 ), ( Percent )sectionActivityEntry.get( 1 ) );
    } );
    return new Activity( overallActivity, activities );
  }

  private void stubActivityProvider( Activity activity ) {
    when( activityProvider.getStatus() ).thenReturn( activity );
  }

  private static List<Object> $( SectionDefinition sectionDefinition, Percent sectionActivity ) {
    return asList( sectionDefinition, sectionActivity );
  }

  private Set<Zone> createZones( SectionDefinition ... sectionDefinitions  ) {
    return Stream.of( sectionDefinitions )
      .map( sectionDefinition -> createZone( stubEntity( sectionDefinition ), adjacency ) )
      .collect( toSet() );
  }

  private void stubAdjacency( SectionDefinition sectionDefinition, Set<Zone> zones ) {
    Zone lookup = zones.stream().filter( zone -> zone.getZoneEntity().getDefinition().equals( sectionDefinition ) ).findAny().get();
    when( adjacency.getZonesOfRelatedPaths( lookup ) ).thenReturn( zones );
  }

  private void stubActivationProvider( Set<Zone> zones ) {
    when( activationProvider.getStatus() ).thenReturn( new Activation( zones ) );
  }
}