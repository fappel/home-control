package com.codeaffine.home.control.application.report;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.WORK_AREA;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.home.control.application.util.ActivityStatus.QUIET;
import static com.codeaffine.home.control.application.util.AllocationStatus.FREQUENT;
import static com.codeaffine.home.control.application.util.MotionStatus.FOCUSSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.application.util.ActivityStatus;
import com.codeaffine.home.control.application.util.AllocationStatus;
import com.codeaffine.home.control.application.util.Analysis;
import com.codeaffine.home.control.application.util.MotionStatus;

public class ActivityReportCompilerTest {

  private ActivityReportCompiler compiler;
  private Analysis analysis;

  @Before
  public void setUp() {
    analysis = mock( Analysis.class );
    compiler = new ActivityReportCompiler( analysis );
  }

  @Test
  public void getOverallActivityReport() {
    stubOverallActivity( P_001 );

    String actual = compiler.getOverallActivityReport();

    assertThat( actual ).contains( QUIET.toString(), P_001.toString() );
  }

  @Test
  public void getReportFor() {
    stubSectionReportValues( WORK_AREA, P_004, P_020, FOCUSSED );

    String actual = compiler.getReportFor( WORK_AREA );

    assertThat( actual )
      .contains( QUIET.toString(), P_004.toString(), FREQUENT.toString(), P_020.toString(), FOCUSSED.toString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAnalysisArgument() {
    new ActivityReportCompiler( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getReportWithNullAsSectionDefinitionArgument() {
    compiler.getReportFor( null );
  }

  private void stubOverallActivity( Percent activity ) {
    when( analysis.getOverallActivity() ).thenReturn( activity );
    when( analysis.getOverallActivityStatus() ).thenReturn( ActivityStatus.valueOf( activity ) );
  }

  private void stubSectionReportValues(
    SectionDefinition sectionDefinition, Percent activity, Percent allocation, MotionStatus motionStatus )
  {
    when( analysis.getActivity( sectionDefinition ) ).thenReturn( activity );
    when( analysis.getActivityStatus( sectionDefinition ) ).thenReturn( ActivityStatus.valueOf( activity ) );
    when( analysis.getAllocation( sectionDefinition ) ).thenReturn( allocation );
    when( analysis.getAllocationStatus( sectionDefinition ) ).thenReturn( AllocationStatus.valueOf( allocation ) );
    when( analysis.getMotionStatus( sectionDefinition ) ).thenReturn( motionStatus );
  }
}