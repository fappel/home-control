package com.codeaffine.home.control.status.internal.util;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.test.util.supplier.StatusSupplierHelper.*;
import static com.codeaffine.home.control.status.type.Percent.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.internal.util.ActivityMath;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.test.util.supplier.StatusSupplierHelper;
import com.codeaffine.home.control.status.type.Percent;

public class ActivityMathTest {

  private ActivationSupplier activationSupplier;
  private ActivitySupplier activitySupplier;
  private StatusSupplierHelper bone;
  private ActivityMath activityMath;

  @Before
  public void setUp() {
    bone = new StatusSupplierHelper();
    activationSupplier = bone.getActivationSupplier();
    activitySupplier = bone.getActivitySupplier();
    activityMath = bone.getActivityMath();
  }

  @Test
  public void calculateGeometricMeanOfPathActivityFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_003 );
  }

  @Test
  public void calculateGeometricMeanOfPathActivityForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateGeometricMeanOfPathActivityForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_003 );
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateGeometricMeanOfPathAllocationForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateGeometricMeanOfPathAllocationFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_005 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForZeroActivations() {
    bone.stubActivitySupplier( newActivity( P_010 ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_000 );
  }

  @Test
  public void calculateArithmeticMeanOfPathActivityForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_005 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForZeroActivations() {
    bone.stubActivitySupplier( newActivity( P_010 ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_000 );
  }

  @Test
  public void calculateArithmeticMeanOfPathAllocationForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateArithmeticMeanOfPathAllocationFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMaximumOfPathActivityFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathActivityForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathActivityForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMaximumOfPathAllocationFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathAllocationForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMaximumOfPathAllocationForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMaximumOfPathAllocationFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMinimumOfPathActivityFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_001 );
  }

  @Test
  public void calculateMinimumOfPathActivityForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMinimumOfPathActivityForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathActivityFor( COOKING_AREA );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void calculateMinimumOfPathAllocationFor() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ), $( LIVING_AREA, P_001 ), $( HALL, P_000 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA, HALL );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_001 );
  }

  @Test
  public void calculateMinimumOfPathAllocationForASingleActivation() {
    bone.stubActivitySupplier( newActivity( P_010, $( WORK_AREA, P_009 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    Optional<Percent> actual = activityMath.calculateMinimumOfPathAllocationFor( WORK_AREA );

    assertThat( actual ).hasValue( P_009 );
  }

  @Test
  public void calculateMinimumOfPathAllocationForNonRelevantSection() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA, LIVING_AREA, HALL ) );

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
  public void constructWithNullAsActivitySupplierArgument() {
    new ActivityMath( null, activationSupplier );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationSupplierArgument() {
    new ActivityMath( activitySupplier, null );
  }
}