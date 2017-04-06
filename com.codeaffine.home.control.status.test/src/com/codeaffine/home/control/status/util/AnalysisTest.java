package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.type.Percent.*;
import static com.codeaffine.home.control.status.util.ActivityStatus.*;
import static com.codeaffine.home.control.status.util.AllocationStatus.*;
import static com.codeaffine.home.control.status.util.AnalysisTestsDoubleHelper.*;
import static com.codeaffine.home.control.status.util.MotionStatus.*;
import static com.codeaffine.home.control.status.util.SunLightStatus.*;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.supplier.SunPosition;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.status.type.Percent;

public class AnalysisTest {

  private MotionStatusCalculator motionStatusCalculator;
  private SunPositionSupplier sunPositionSupplier;
  private AnalysisTestsDoubleHelper bone;
  private Analysis analysis;

  @Before
  public void setUp() {
    motionStatusCalculator = mock( MotionStatusCalculator.class );
    sunPositionSupplier = mock( SunPositionSupplier.class );
    bone = new AnalysisTestsDoubleHelper();
    analysis = newAnalysis();
  }

  @Test
  public void getOverallActivity() {
    bone.stubActivitySupplier( newActivity( P_003 ) );

    Percent actual = analysis.getOverallActivity();

    assertThat( actual ).isSameAs( P_003 );
  }

  @Test
  public void getOverallActivityStatus() {
    bone.stubActivitySupplier( newActivity( QUIET.threshold ) );

    ActivityStatus actual = analysis.getOverallActivityStatus();

    assertThat( actual ).isSameAs( QUIET );
  }

  @Test
  public void isOverallActivityStatusSameAs() {
    bone.stubActivitySupplier( newActivity( QUIET.threshold ) );

    boolean same = analysis.isOverallActivityStatusSameAs( QUIET );
    boolean larger = analysis.isOverallActivityStatusSameAs( IDLE );
    boolean smaller = analysis.isOverallActivityStatusSameAs( AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isOverallActivityStatusAtLeast() {
    bone.stubActivitySupplier( newActivity( QUIET.threshold ) );

    boolean same = analysis.isOverallActivityStatusAtLeast( QUIET );
    boolean larger = analysis.isOverallActivityStatusAtLeast( IDLE );
    boolean smaller = analysis.isOverallActivityStatusAtLeast( AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isOverallActivityStatusAtMost() {
    bone.stubActivitySupplier( newActivity( QUIET.threshold ) );

    boolean same = analysis.isOverallActivityStatusAtMost( QUIET );
    boolean larger = analysis.isOverallActivityStatusAtMost( IDLE );
    boolean smaller = analysis.isOverallActivityStatusAtMost( AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isTrue();
  }

  @Test
  public void getActivity() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, P_003 ) ) );

    Percent actual = analysis.getActivity( WORK_AREA );

    assertThat( actual ).isSameAs( P_003 );
  }

  @Test
  public void getActivityOfSectionThatHasNoActivity() {
    bone.stubActivitySupplier( newActivity( P_003 ) );

    Percent actual = analysis.getActivity( WORK_AREA );

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void getActivityStatus() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    ActivityStatus actual = analysis.getActivityStatus( WORK_AREA );

    assertThat( actual ).isSameAs( QUIET );
  }

  @Test
  public void isActivityStatusSameAs() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    boolean same = analysis.isActivityStatusSameAs( WORK_AREA, QUIET );
    boolean larger = analysis.isActivityStatusSameAs( WORK_AREA, IDLE );
    boolean smaller = analysis.isActivityStatusSameAs( WORK_AREA, AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isActivityStatusAtLeast() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    boolean same = analysis.isActivityStatusAtLeast( WORK_AREA, QUIET );
    boolean larger = analysis.isActivityStatusAtLeast( WORK_AREA, IDLE );
    boolean smaller = analysis.isActivityStatusAtLeast( WORK_AREA, AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isActivityStatusAtMost() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    boolean same = analysis.isActivityStatusAtMost( WORK_AREA, QUIET );
    boolean larger = analysis.isActivityStatusAtMost( WORK_AREA, IDLE );
    boolean smaller = analysis.isActivityStatusAtMost( WORK_AREA, AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isTrue();
  }

  @Test
  public void getAllocation() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, P_003 ) ) );

    Percent actual = analysis.getAllocation( WORK_AREA );

    assertThat( actual ).isSameAs( P_003 );
  }

  @Test
  public void getAllocationOfSectionThatHasNoAllocation() {
    bone.stubActivitySupplier( newActivity( P_003 ) );

    Percent actual = analysis.getAllocation( WORK_AREA );

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void getAllocationStatus() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

    AllocationStatus actual = analysis.getAllocationStatus( WORK_AREA );

    assertThat( actual ).isSameAs( RARE );
  }

  @Test
  public void isAllocationStatusSameAs() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

    boolean same = analysis.isAllocationStatusSameAs( WORK_AREA, RARE );
    boolean larger = analysis.isAllocationStatusSameAs( WORK_AREA, UNUSED );
    boolean smaller = analysis.isAllocationStatusSameAs( WORK_AREA, OCCASIONAL );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isAllocationStatusAtLeast() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

    boolean same = analysis.isAllocationStatusAtLeast( WORK_AREA, RARE );
    boolean larger = analysis.isAllocationStatusAtLeast( WORK_AREA, UNUSED );
    boolean smaller = analysis.isAllocationStatusAtLeast( WORK_AREA, OCCASIONAL );

    assertThat( same ).isTrue();
    assertThat( larger ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isAllocationStatusAtMost() {
    bone.stubActivitySupplier( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

    boolean same = analysis.isAllocationStatusAtMost( WORK_AREA, RARE );
    boolean larger = analysis.isAllocationStatusAtMost( WORK_AREA, UNUSED );
    boolean smaller = analysis.isAllocationStatusAtMost( WORK_AREA, OCCASIONAL );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isTrue();
  }

  @Test
  public void getMotionStatus() {
    stubMotionStatusCalculator( WORK_AREA, FOCUSSED );

    MotionStatus actual = analysis.getMotionStatus( WORK_AREA );

    assertThat( actual ).isSameAs( FOCUSSED );
  }

  @Test
  public void isMotionStatusSameAs() {
    stubMotionStatusCalculator( WORK_AREA, FOCUSSED );

    boolean same = analysis.isMotionStatusSameAs( WORK_AREA, FOCUSSED );
    boolean larger = analysis.isMotionStatusSameAs( WORK_AREA, EVEN );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
  }

  @Test
  public void isMotionStatusAtLeast() {
    stubMotionStatusCalculator( WORK_AREA, EVEN );

    boolean same = analysis.isMotionStatusAtLeast( WORK_AREA, EVEN );
    boolean smaller = analysis.isMotionStatusAtLeast( WORK_AREA, FOCUSSED );

    assertThat( same ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isMotionStatusSameAtMost() {
    stubMotionStatusCalculator( WORK_AREA, EVEN );

    boolean same = analysis.isMotionStatusAtMost( WORK_AREA, EVEN );
    boolean smaller = analysis.isMotionStatusAtMost( WORK_AREA, FOCUSSED );

    assertThat( same ).isTrue();
    assertThat( smaller ).isTrue();
  }

  @Test
  public void isZoneActivated() {
    bone.stubActivationSupplier( bone.createZones( WORK_AREA ) );

    boolean actual = analysis.isZoneActivated( WORK_AREA );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentZoneActivated() {
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationSupplier( zones );

    boolean actual = analysis.isAdjacentZoneActivated( WORK_AREA );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentZoneActivatedIfZoneIsNotPresent() {
    bone.stubActivationSupplier( emptySet() );

    boolean actual = analysis.isAdjacentZoneActivated( WORK_AREA );

    assertThat( actual ).isFalse();
  }

  @Test
  public void getSunPosition() {
    stubSunPositionSupplier( 12.0, 14.0 );

    SunPosition actual = analysis.getSunPosition();

    assertThat( actual ).isEqualTo( new SunPosition( 12.0, 14.0 ) );
  }

  @Test
  public void getSunLightStatus() {
    stubSunPositionSupplier( TWILIGHT.threshold );

    SunLightStatus actual = analysis.getSunLightStatus();

    assertThat( actual ).isSameAs( TWILIGHT );
  }

  @Test
  public void isSunLightStatusSameAs() {
    stubSunPositionSupplier( TWILIGHT.threshold );

    boolean same = analysis.isSunLightStatusSameAs( TWILIGHT );
    boolean larger = analysis.isSunLightStatusSameAs( NIGHT );
    boolean smaller = analysis.isSunLightStatusSameAs( DAY );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isSunLightStatusAtLeast() {
    stubSunPositionSupplier( TWILIGHT.threshold );

    boolean same = analysis.isSunLightStatusAtLeast( TWILIGHT );
    boolean larger = analysis.isSunLightStatusAtLeast( NIGHT );
    boolean smaller = analysis.isSunLightStatusAtLeast( DAY );

    assertThat( same ).isTrue();
    assertThat( larger ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isSunLightStatusAtMost() {
    stubSunPositionSupplier( TWILIGHT.threshold );

    boolean same = analysis.isSunLightStatusAtMost( TWILIGHT );
    boolean larger = analysis.isSunLightStatusAtMost( NIGHT );
    boolean smaller = analysis.isSunLightStatusAtMost( DAY );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isTrue();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsMotionStatusCalculatorArgument() {
    new Analysis( null, sunPositionSupplier, bone.getActivationSupplier(), bone.getActivitySupplier() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSunPositionSupplierArgument() {
    new Analysis( motionStatusCalculator, null, bone.getActivationSupplier(), bone.getActivitySupplier() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationSupplierArgument() {
    new Analysis( motionStatusCalculator, sunPositionSupplier, null, bone.getActivitySupplier() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivitySupplierArgument() {
    new Analysis( motionStatusCalculator, sunPositionSupplier, bone.getActivationSupplier(), null );
  }

  private void stubMotionStatusCalculator( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    when( motionStatusCalculator.getMotionStatus( sectionDefinition ) ).thenReturn( motionStatus );
  }

  private void stubSunPositionSupplier( double zenit ) {
    stubSunPositionSupplier( zenit, 0.0D );
  }

  private void stubSunPositionSupplier( double zenit, double azimuth ) {
    when( sunPositionSupplier.getStatus() ).thenReturn( new SunPosition( zenit, azimuth ) );
  }

  private Analysis newAnalysis() {
    return new Analysis( motionStatusCalculator,
                         sunPositionSupplier,
                         bone.getActivationSupplier(),
                         bone.getActivitySupplier() );
  }
}