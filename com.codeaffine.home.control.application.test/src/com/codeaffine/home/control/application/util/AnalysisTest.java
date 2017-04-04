package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.home.control.application.util.ActivityStatus.*;
import static com.codeaffine.home.control.application.util.AllocationStatus.*;
import static com.codeaffine.home.control.application.util.AnalysisTestsDoubleHelper.*;
import static com.codeaffine.home.control.application.util.MotionStatus.*;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;

public class AnalysisTest {

  private MotionStatusCalculator motionStatusCalculator;
  private AnalysisTestsDoubleHelper bone;
  private Analysis analysis;

  @Before
  public void setUp() {
    motionStatusCalculator = mock( MotionStatusCalculator.class );
    bone = new AnalysisTestsDoubleHelper();
    analysis = new Analysis( motionStatusCalculator, bone.getActivationProvider(), bone.getActivityProvider() );
  }

  @Test
  public void getOverallActivity() {
    bone.stubActivityProvider( newActivity( P_003 ) );

    Percent actual = analysis.getOverallActivity();

    assertThat( actual ).isSameAs( P_003 );
  }

  @Test
  public void getOverallActivityStatus() {
    bone.stubActivityProvider( newActivity( QUIET.threshold ) );

    ActivityStatus actual = analysis.getOverallActivityStatus();

    assertThat( actual ).isSameAs( QUIET );
  }

  @Test
  public void isOverallActivityStatusSameAs() {
    bone.stubActivityProvider( newActivity( QUIET.threshold ) );

    boolean same = analysis.isOverallActivityStatusSameAs( QUIET );
    boolean larger = analysis.isOverallActivityStatusSameAs( IDLE );
    boolean smaller = analysis.isOverallActivityStatusSameAs( AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isOverallActivityStatusAtLeast() {
    bone.stubActivityProvider( newActivity( QUIET.threshold ) );

    boolean same = analysis.isOverallActivityStatusAtLeast( QUIET );
    boolean larger = analysis.isOverallActivityStatusAtLeast( IDLE );
    boolean smaller = analysis.isOverallActivityStatusAtLeast( AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isOverallActivityStatusAtMost() {
    bone.stubActivityProvider( newActivity( QUIET.threshold ) );

    boolean same = analysis.isOverallActivityStatusAtMost( QUIET );
    boolean larger = analysis.isOverallActivityStatusAtMost( IDLE );
    boolean smaller = analysis.isOverallActivityStatusAtMost( AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isTrue();
  }

  @Test
  public void getActivity() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, P_003 ) ) );

    Percent actual = analysis.getActivity( WORK_AREA );

    assertThat( actual ).isSameAs( P_003 );
  }

  @Test
  public void getActivityOfSectionThatHasNoActivity() {
    bone.stubActivityProvider( newActivity( P_003 ) );

    Percent actual = analysis.getActivity( WORK_AREA );

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void getActivityStatus() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    ActivityStatus actual = analysis.getActivityStatus( WORK_AREA );

    assertThat( actual ).isSameAs( QUIET );
  }

  @Test
  public void isActivityStatusSameAs() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    boolean same = analysis.isActivityStatusSameAs( WORK_AREA, QUIET );
    boolean larger = analysis.isActivityStatusSameAs( WORK_AREA, IDLE );
    boolean smaller = analysis.isActivityStatusSameAs( WORK_AREA, AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isActivityStatusAtLeast() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    boolean same = analysis.isActivityStatusAtLeast( WORK_AREA, QUIET );
    boolean larger = analysis.isActivityStatusAtLeast( WORK_AREA, IDLE );
    boolean smaller = analysis.isActivityStatusAtLeast( WORK_AREA, AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isActivityStatusAtMost() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, QUIET.threshold ) ) );

    boolean same = analysis.isActivityStatusAtMost( WORK_AREA, QUIET );
    boolean larger = analysis.isActivityStatusAtMost( WORK_AREA, IDLE );
    boolean smaller = analysis.isActivityStatusAtMost( WORK_AREA, AROUSED );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isTrue();
  }

  @Test
  public void getAllocation() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, P_003 ) ) );

    Percent actual = analysis.getAllocation( WORK_AREA );

    assertThat( actual ).isSameAs( P_003 );
  }

  @Test
  public void getAllocationOfSectionThatHasNoAllocation() {
    bone.stubActivityProvider( newActivity( P_003 ) );

    Percent actual = analysis.getAllocation( WORK_AREA );

    assertThat( actual ).isSameAs( P_000 );
  }

  @Test
  public void getAllocationStatus() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

    AllocationStatus actual = analysis.getAllocationStatus( WORK_AREA );

    assertThat( actual ).isSameAs( RARE );
  }

  @Test
  public void isAllocationStatusSameAs() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

    boolean same = analysis.isAllocationStatusSameAs( WORK_AREA, RARE );
    boolean larger = analysis.isAllocationStatusSameAs( WORK_AREA, UNUSED );
    boolean smaller = analysis.isAllocationStatusSameAs( WORK_AREA, OCCASIONAL );

    assertThat( same ).isTrue();
    assertThat( larger ).isFalse();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isAllocationStatusAtLeast() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

    boolean same = analysis.isAllocationStatusAtLeast( WORK_AREA, RARE );
    boolean larger = analysis.isAllocationStatusAtLeast( WORK_AREA, UNUSED );
    boolean smaller = analysis.isAllocationStatusAtLeast( WORK_AREA, OCCASIONAL );

    assertThat( same ).isTrue();
    assertThat( larger ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isAllocationStatusAtMost() {
    bone.stubActivityProvider( newActivity( P_000, $( WORK_AREA, RARE.threshold ) ) );

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
    bone.stubActivationProvider( bone.createZones( WORK_AREA ) );

    boolean actual = analysis.isZoneActivated( WORK_AREA );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentZoneActivated() {
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    boolean actual = analysis.isAdjacentZoneActivated( WORK_AREA );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isAdjacentZoneActivatedIfZoneIsNotPresent() {
    bone.stubActivationProvider( emptySet() );

    boolean actual = analysis.isAdjacentZoneActivated( WORK_AREA );

    assertThat( actual ).isFalse();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsMotionStatusCalculatorArgument() {
    new Analysis( null, mock( ActivationProvider.class ), mock( ActivityProvider.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationProviderArgument() {
    new Analysis( mock( MotionStatusCalculator.class ), null, mock( ActivityProvider.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivityProviderArgument() {
    new Analysis( mock( MotionStatusCalculator.class ), mock( ActivationProvider.class ), null );
  }

  private void stubMotionStatusCalculator( SectionDefinition sectionDefinition, MotionStatus motionStatus ) {
    when( motionStatusCalculator.getMotionStatus( sectionDefinition ) ).thenReturn( motionStatus );
  }
}