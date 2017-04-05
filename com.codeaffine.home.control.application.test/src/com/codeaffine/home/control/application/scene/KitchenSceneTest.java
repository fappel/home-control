package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.KitchenScene.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.ActivityStatus.LIVELY;
import static com.codeaffine.home.control.status.util.AllocationStatus.UNUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.util.LampControl;

public class KitchenSceneTest {

  private LampControl lampControl;
  private AnalysisStub analysis;
  private KitchenScene scene;

  @Before
  public void setUp() {
    lampControl = mock( LampControl.class );
    analysis = new AnalysisStub();
    scene = new KitchenScene( lampControl, analysis.getStub() );
  }

  @Test
  public void prepareIfCookingAreaAllocationIsHighEnough() {
    analysis.stubIsAllocationStatusAtLeast( COOKING_AREA, COOKING_AREA_ALLOCATION_THRESHOLD, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( COOKING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfSingleActivationOfCookingArea() {
    analysis.stubIsAdjacentZoneActivated( COOKING_AREA, false );
    analysis.stubIsZoneActivated( COOKING_AREA, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( COOKING_AREA );
    verifyNoMoreInteractions( lampControl );
  }


  @Test
  public void prepareOnMultipleActivationIncludingCookingArea() {
    analysis.stubIsAdjacentZoneActivated( COOKING_AREA, true );
    analysis.stubIsZoneActivated( COOKING_AREA, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( DINING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfSingleActivationOfCookingAreaAndCookingAreaAllocationIsHighEnough() {
    analysis.stubIsAllocationStatusAtLeast( COOKING_AREA, COOKING_AREA_ALLOCATION_THRESHOLD, true );
    analysis.stubIsAdjacentZoneActivated( COOKING_AREA, false );
    analysis.stubIsZoneActivated( COOKING_AREA, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( COOKING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfCookingAreaIsNotHot() {
    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( DINING_AREA );
  }

  @Test
  public void prepareIfCookingAreaIsNotHotButOverallActivityAndDiningAreaAllocationIsHighEnough() {
    analysis.stubIsAllocationStatusAtLeast( DINING_AREA, DINING_AREA_ALLOCATION_THRESHOLD, true );
    analysis.stubIsOverallActivityStatusAtLeast( LIVELY, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( DINING_AREA );
  }

  @Test
  public void prepareIfDiningAreaIsHotButAllocationStatusIsLow() {
    analysis.stubIsAllocationStatusAtLeast( DINING_AREA, UNUSED, true );
    analysis.stubIsOverallActivityStatusAtLeast( DINING_AREA_OVERALL_ACTIVITY_THRESHOLD, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( DINING_AREA );
  }

  @Test
  public void prepareIfCookingAreaIsHotAndDiningAreaAllocationIsHighEnough() {
    analysis.stubIsAllocationStatusAtLeast( COOKING_AREA, COOKING_AREA_ALLOCATION_THRESHOLD, true );
    analysis.stubIsAllocationStatusAtLeast( DINING_AREA, DINING_AREA_ALLOCATION_THRESHOLD, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( COOKING_AREA );
    verify( lampControl ).setZoneLampsForFiltering( DINING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfCookingAreaAndDiningAreaAreHot() {
    analysis.stubIsAllocationStatusAtLeast( COOKING_AREA, COOKING_AREA_ALLOCATION_THRESHOLD, true );
    analysis.stubIsZoneActivated( DINING_AREA, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( COOKING_AREA );
    verify( lampControl ).setZoneLampsForFiltering( DINING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfCookingAreaAndDiningAreaAreHotWithHighEnoughAllocationAndOverallActivity() {
    analysis.stubIsAllocationStatusAtLeast( DINING_AREA, DINING_AREA_ALLOCATION_THRESHOLD, true );
    analysis.stubIsOverallActivityStatusAtLeast( LIVELY, true );
    analysis.stubIsAllocationStatusAtLeast( COOKING_AREA, COOKING_AREA_ALLOCATION_THRESHOLD, true );
    analysis.stubIsZoneActivated( DINING_AREA, true );

    scene.prepare();

    verify( lampControl ).switchOnZoneLamps( COOKING_AREA, DINING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfCookingAreaGetsColdWithinTimeout() {
    analysis.stubIsAdjacentZoneActivated( COOKING_AREA, false );
    analysis.stubIsZoneActivated( COOKING_AREA, true );
    analysis.stubIsZoneActivated( DINING_AREA, true );
    scene.prepare();
    analysis.stubIsZoneActivated( COOKING_AREA, false );
    reset( lampControl );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( COOKING_AREA, DINING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfDiningAreaGetsColdWithinTimeout() {
    analysis.stubIsAdjacentZoneActivated( COOKING_AREA, false );
    analysis.stubIsZoneActivated( COOKING_AREA, true );
    analysis.stubIsZoneActivated( DINING_AREA, true );
    scene.prepare();
    analysis.stubIsZoneActivated( DINING_AREA, false );
    reset( lampControl );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( COOKING_AREA, DINING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void getName() {
    String actual = scene.getName();

    assertThat( actual ).isEqualTo( KitchenScene.class.getSimpleName() );
  }
}