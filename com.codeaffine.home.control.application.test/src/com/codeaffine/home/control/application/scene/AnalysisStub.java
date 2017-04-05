package com.codeaffine.home.control.application.scene;

import static org.mockito.Mockito.*;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.util.ActivityStatus;
import com.codeaffine.home.control.application.util.AllocationStatus;
import com.codeaffine.home.control.application.util.Analysis;

class AnalysisStub {

  private final Analysis analysis;

  public AnalysisStub() {
    analysis = mock( Analysis.class );
  }

  Analysis getStub() {
    return analysis;
  }

  void stubIsAdjacentZoneActivated( SectionDefinition sectionDefinition, boolean result ) {
    when( analysis.isAdjacentZoneActivated( sectionDefinition ) ).thenReturn( result );
  }

  void stubIsZoneActivated( SectionDefinition sectionDefinition, boolean result ) {
    when( analysis.isZoneActivated( sectionDefinition ) ).thenReturn( result );
  }

  void stubIsAllocationStatusAtLeast( SectionDefinition sectionDefinition, AllocationStatus status, boolean result ) {
    when( analysis.isAllocationStatusAtLeast( sectionDefinition, status ) ).thenReturn( result );
  }

  void stubIsOverallActivityStatusAtLeast( ActivityStatus status, boolean result ) {
    when( analysis.isOverallActivityStatusAtLeast( status ) ).thenReturn( result );
  }
}