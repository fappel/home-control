package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.LivingRoomScene.*;
import static com.codeaffine.home.control.application.util.TimeoutPreferenceHelper.stubPreference;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.type.OnOff.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.type.OnOff;
import com.codeaffine.home.control.application.test.AnalysisStub;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.TimeoutPreference;

public class LivingRoomSceneTest {

  private ComputerStatusSupplier computerStatusSupplier;
  private LampControl lampControl;
  private LivingRoomScene scene;
  private AnalysisStub analysis;

  @Before
  public void setUp() {
    computerStatusSupplier = mock( ComputerStatusSupplier.class );
    lampControl = mock( LampControl.class );
    analysis = new AnalysisStub();
    TimeoutPreference preference = stubPreference( 1L, MINUTES );
    scene = new LivingRoomScene( lampControl, computerStatusSupplier, analysis.getStub(), preference );
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
    analysis.stubIsAllocationStatusAtLeast( WORK_AREA, WORK_AREA_ALLOCATION_THRESHOLD, true );

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
    analysis.stubIsAllocationStatusAtLeast( LIVING_AREA, LIVING_AREA_ALLOCATION_THRESHOLD, true );

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
    when( computerStatusSupplier.getStatus() ).thenReturn( status );
  }
}