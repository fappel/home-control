package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.type.Percent.*;
import static com.codeaffine.home.control.status.util.AnalysisTestsDoubleHelper.*;
import static com.codeaffine.home.control.status.util.MotionStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.Activation.Zone;

public class MotionStatusCalculatorTest {

  private AnalysisTestsDoubleHelper bone;
  private MotionStatusCalculator calculator;

  @Before
  public void setUp() {
    bone = new AnalysisTestsDoubleHelper();
    calculator = new MotionStatusCalculator( bone.getActivationProvider(), bone.getActivityProvider() );
  }

  @Test
  public void getMotionStatus() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_050 ), $( LIVING_AREA, P_001 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    MotionStatus actual = calculator.getMotionStatus( WORK_AREA );

    assertThat( actual ).isSameAs( FOCUSSED );
  }

  @Test
  public void getMotionStatusIfMeanDeltaIsTooSmall() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_050 ), $( LIVING_AREA, P_021 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    MotionStatus actual = calculator.getMotionStatus( WORK_AREA );

    assertThat( actual ).isSameAs( EVEN );
  }

  @Test
  public void getMotionStatusIfSectionHasNotMaximumAllocation() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_050 ), $( LIVING_AREA, P_021 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    MotionStatus actual = calculator.getMotionStatus( LIVING_AREA );

    assertThat( actual ).isSameAs( EVEN );
  }

  @Test
  public void getMotionStatusIfSectionIsNotActivated() {
    bone.stubActivityProvider( newActivity( P_010, $( WORK_AREA, P_050 ), $( LIVING_AREA, P_021 ) ) );
    Set<Zone> zones = bone.createZones( WORK_AREA, LIVING_AREA );
    bone.stubAdjacency( WORK_AREA, zones );
    bone.stubActivationProvider( zones );

    MotionStatus actual = calculator.getMotionStatus( HALL );

    assertThat( actual ).isSameAs( EVEN );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getMotionStatusWithNullAsSectionDefinitionArgument() {
    calculator.getMotionStatus( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivationProviderArgument() {
    new MotionStatusCalculator( null, mock( ActivitySupplier.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActivityProviderArgument() {
    new MotionStatusCalculator( mock( ActivationSupplier.class ), null );
  }
}