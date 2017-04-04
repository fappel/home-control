package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.home.control.application.util.AllocationStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.ComputerStatusProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.application.util.LampControl;

public class LivingRoomSceneTest {

  private ComputerStatusProvider computerStatusProvider;
  private LampControl lampControl;
  private LivingRoomScene scene;
  private AnalysisStub analysis;

  @Before
  public void setUp() {
    computerStatusProvider = mock( ComputerStatusProvider.class );
    lampControl = mock( LampControl.class );
    analysis = new AnalysisStub();
    scene = new LivingRoomScene( lampControl, computerStatusProvider, analysis.getStub() );
  }

  @Test
  public void prepareIfComputerIsActive() {
    stubComputerStatusProvider( ON );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( WORK_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfSingleActivationOfWorkArea() {
    analysis.stubIsZoneActivated( WORK_AREA, true );
    analysis.stubIsAdjacentZoneActivated( WORK_AREA, false );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( WORK_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfAllocationStatusOfWorkAreaIsHighEnough() {
    analysis.stubIsAllocationStatusAtLeast( WORK_AREA, PERMANENT, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( WORK_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfWorkAreaIsNotHot() {
    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( LIVING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfWorkAreaIsActiveButNotHot() {
    analysis.stubIsZoneActivated( WORK_AREA, true );
    analysis.stubIsAdjacentZoneActivated( WORK_AREA, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( LIVING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfWorkAreaIsHotAndAllocationStatusOfLivingAreaIsHighEnough() {
    stubComputerStatusProvider( ON );
    analysis.stubIsAllocationStatusAtLeast( LIVING_AREA, CONTINUAL, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( WORK_AREA );
    verify( lampControl ).setZoneLampsForFiltering( LIVING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfWorkAreaIsHotAndLivingAreaIsActivated() {
    stubComputerStatusProvider( ON );
    analysis.stubIsZoneActivated( LIVING_AREA, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( WORK_AREA );
    verify( lampControl ).setZoneLampsForFiltering( LIVING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfWorkAreaGetsColdWithinTimeout() {
    stubComputerStatusProvider( ON );
    analysis.stubIsZoneActivated( LIVING_AREA, true );
    scene.prepare();
    stubComputerStatusProvider( OFF );
    reset( lampControl );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( WORK_AREA );
    verify( lampControl ).setZoneLampsForFiltering( LIVING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfLivingAreaGetsColdWithinTimeout() {
    stubComputerStatusProvider( ON );
    analysis.stubIsZoneActivated( LIVING_AREA, true );
    scene.prepare();
    analysis.stubIsZoneActivated( LIVING_AREA, false );
    reset( lampControl );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( WORK_AREA );
    verify( lampControl ).setZoneLampsForFiltering( LIVING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void getName() {
    String actual = scene.getName();

    assertThat( actual ).isEqualTo( LivingRoomScene.class.getSimpleName() );
  }

  private void stubComputerStatusProvider( OnOff status ) {
    when( computerStatusProvider.getStatus() ).thenReturn( status );
  }
}