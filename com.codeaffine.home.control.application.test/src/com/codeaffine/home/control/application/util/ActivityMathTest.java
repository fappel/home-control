package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.home.control.application.util.AnalysisTestsDoubleHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;

public class ActivityMathTest {

  private ActivationProvider activationProvider;
  private ActivityProvider activityProvider;
  private AnalysisTestsDoubleHelper bone;
  private ActivityMath activityMath;

  @Before
  public void setUp() {
    bone = new AnalysisTestsDoubleHelper();
    activationProvider = bone.getActivationProvider();
    activityProvider = bone.getActivityProvider();
    activityMath = bone.getActivityMath();
  }

  @Test
  public void calculateGeometricMeanOfPathActivityFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_003 );
  }

  @Test
  public void calculateGeometricMeanOfPathActivityForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateGeometricMeanOfPathActivityForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_003 );
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_005 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForZeroActivations() {
    bone.stubActivityProvider( newActivity( P_010 ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_000 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_005 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForZeroActivations() {
    bone.stubActivityProvider( newActivity( P_010 ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_000 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMaximumOfPathActivityFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathActivityForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathActivityForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMaximumOfPathAllocationFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathAllocationForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathAllocationForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMinimumOfPathActivityFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_001 );
  }

  @Test
  public void calculateMinimumOfPathActivityForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMinimumOfPathActivityForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMinimumOfPathAllocationFor() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_001 );
  }

  @Test
  public void calculateMinimumOfPathAllocationForASingleActivation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMinimumOfPathAllocationForNonRelevantSection() {
    bone.stubActivationProvider( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

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
}