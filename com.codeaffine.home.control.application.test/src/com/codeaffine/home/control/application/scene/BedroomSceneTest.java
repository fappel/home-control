package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BedStand;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubSection;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.stubZone;
import static com.codeaffine.home.control.status.util.ActivityStatus.LIVELY;
import static com.codeaffine.home.control.status.util.AllocationStatus.FREQUENT;
import static com.codeaffine.home.control.status.util.SunLightStatus.NIGHT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.test.AnalysisStub;
import com.codeaffine.home.control.application.util.LampControl;

public class BedroomSceneTest {

  private LampControl lampControl;
  private AnalysisStub analysis;
  private BedroomScene scene;

  @Before
  public void setUp() {
    lampControl = mock( LampControl.class );
    analysis = new AnalysisStub();
    scene = new BedroomScene( lampControl, analysis.getStub() );
  }

  @Test
  public void prepareIfDressingAreaIsHot() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfDressingAreaIsHotAndBedIsActivated() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );
    analysis.stubIsZoneActivated( BED, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verify( lampControl ).switchOnLamps( BedStand );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfDressingAreaIsHotAndBedSideIsActivated() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );
    analysis.stubIsZoneActivated( BED_SIDE, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verify( lampControl ).switchOnLamps( BedStand );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfDressingAreaIsHotAndBedIsActivatedAndBedSideIsLively() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );
    analysis.stubIsZoneActivated( BED, true );
    analysis.stubIsActivityStatusAtLeast( BED_SIDE, LIVELY, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verify( lampControl ).switchOnLamps( BedStand );
    verify( lampControl ).switchOnZoneLamps( BED_SIDE );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfDressingAreaIsHotAndBedSideIsActivatedAndLively() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );
    analysis.stubIsZoneActivated( BED_SIDE, true );
    analysis.stubIsActivityStatusAtLeast( BED_SIDE, LIVELY, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verify( lampControl ).switchOnLamps( BedStand );
    verify( lampControl ).switchOnZoneLamps( BED_SIDE );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfBedAreaIsHot() {
    analysis.stubGetActivatedZones( asSet( stubZone( stubSection( BED ) ), stubZone( stubSection( BED_SIDE ) ) ) );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfRoomIsHotAndDressingAreaIsUsedALotAtNight() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );
    analysis.stubIsSunLightStatusAtMost( NIGHT, true );
    analysis.stubIsAllocationStatusAtLeast( DRESSING_AREA, FREQUENT, true );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verify( lampControl ).switchOnZoneLamps( DRESSING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfRoomIsHotAndDressingAreaIsUsedNotMuchAtNight() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );
    analysis.stubIsSunLightStatusAtMost( NIGHT, true );
    analysis.stubIsAllocationStatusAtLeast( DRESSING_AREA, FREQUENT, false );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfRoomIsCold() {
    scene.prepare();

    verify( lampControl ).switchOffZoneLamps( BED, BED_SIDE, DRESSING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void prepareIfPreviouslyHotRoomGetsColdWithinTimeout() {
    analysis.stubIsZoneActivated( DRESSING_AREA, true );
    scene.prepare();
    analysis.stubIsZoneActivated( DRESSING_AREA, false );
    reset( lampControl );

    scene.prepare();

    verify( lampControl ).setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    verifyNoMoreInteractions( lampControl );
  }

  @Test
  public void getName() {
    String actual = scene.getName();

    assertThat( actual ).isEqualTo( BedroomScene.class.getSimpleName() );
  }
}