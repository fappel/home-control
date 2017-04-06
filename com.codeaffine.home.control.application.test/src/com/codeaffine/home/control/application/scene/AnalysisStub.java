package com.codeaffine.home.control.application.scene;

import static org.mockito.Mockito.*;

import java.util.Set;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.util.ActivityStatus;
import com.codeaffine.home.control.status.util.AllocationStatus;
import com.codeaffine.home.control.status.util.Analysis;
import com.codeaffine.home.control.status.util.SunLightStatus;

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

  void stubGetActivatedZones( Set<Zone> zones ) {
    when( analysis.getActivatedZones() ).thenReturn( zones );
  }

  void stubIsOverallActivityStatusAtLeast( ActivityStatus status, boolean result ) {
    when( analysis.isOverallActivityStatusAtLeast( status ) ).thenReturn( result );
  }

  void stubIsActivityStatusAtLeast( SectionDefinition sectionDefinition, ActivityStatus status, boolean result ) {
    when( analysis.isActivityStatusAtLeast( sectionDefinition, status ) ).thenReturn( result );
  }

  void stubIsAllocationStatusAtLeast( SectionDefinition sectionDefinition, AllocationStatus status, boolean result ) {
    when( analysis.isAllocationStatusAtLeast( sectionDefinition, status ) ).thenReturn( result );
  }

  void stubIsSunLightStatusAtMost( SunLightStatus status, boolean result ) {
    when( analysis.isSunLightStatusAtMost( status ) ).thenReturn( result );
  }
}