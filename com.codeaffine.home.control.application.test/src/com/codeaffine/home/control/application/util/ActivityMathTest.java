package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.ActivationHelper.createZone;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
import com.codeaffine.home.control.application.util.ActivityMath;

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
  public void calculateGeometricMeanOfPathAllocationFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_003 );
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( COOKING_AREA );

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
  public void calculateArithmeticMeanOfPathActivityForZeroActivations() {
    stubActivityProvider( newActivity( P_010 ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_000 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_005 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForZeroActivations() {
    stubActivityProvider( newActivity( P_010 ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_000 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( COOKING_AREA );

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
  public void calculateMaximumOfPathAllocationFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathAllocationForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathAllocationForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( COOKING_AREA );

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

  @Test
  public void calculateMinimumOfPathAllocationFor() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = createZones( WORK_AREA, LIVING_AREA, HALL );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_001 );
  }

  @Test
  public void calculateMinimumOfPathAllocationForASingleActivation() {
    stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = createZones( WORK_AREA );
    stubAdjacency( WORK_AREA, zones );
    stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMinimumOfPathAllocationForNonRelevantSection() {
    stubActivationProvider( createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathAllocationFor( COOKING_AREA );

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
  public void calculateGeometricMeanOfPathAllocationForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateGeometricMeanOfPathAllocationFor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void calculateArithmeticMeanOfPathAllocationForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateArithmeticMeanOfPathAllocationFor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void calculateMaximumOfPathAllocationForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateMaximumOfPathAllocationFor( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void calculateMinimumOfPathAllocationForWithNullAsSectionDefinitionArgument() {
    activityMath.calculateMinimumOfPathAllocationFor( null );
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
  private static Activity newActivity( Percent overallActivity, List<Object> ... activationEntries ) {
    Map<SectionDefinition, Percent> activations = collectActivations( activationEntries );
    return new Activity( overallActivity, activations, activations );
  }

  @SafeVarargs
  private static Map<SectionDefinition, Percent> collectActivations( List<Object>... activationEntries ) {
    return Stream.of( activationEntries )
      .collect( toMap( activationEntry -> ( SectionDefinition )activationEntry.get( 0 ),
                       activationEntry -> ( Percent )activationEntry.get( 1 ) ) );
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