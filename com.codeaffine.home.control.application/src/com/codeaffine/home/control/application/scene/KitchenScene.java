package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.ActivityStatus.LIVELY;
import static com.codeaffine.home.control.status.util.AllocationStatus.FREQUENT;

import java.util.ArrayList;
import java.util.List;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.util.ActivityStatus;
import com.codeaffine.home.control.status.util.AllocationStatus;
import com.codeaffine.home.control.status.util.Analysis;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;

class KitchenScene implements Scene {

  static final AllocationStatus DINING_AREA_ALLOCATION_THRESHOLD = FREQUENT;
  static final AllocationStatus COOKING_AREA_ALLOCATION_THRESHOLD = FREQUENT;
  static final ActivityStatus DINING_AREA_OVERALL_ACTIVITY_THRESHOLD = LIVELY;

  private final Timeout cookingAreaTimeout;
  private final Timeout diningAreaTimeout;
  private final LampControl lampControl;
  private final Analysis analysis;
  private final List<SectionDefinition> zonesToSwitchOn;
  private final List<SectionDefinition> zonesForFiltering;

  KitchenScene( LampControl lampControl, Analysis analysis ) {
    this.lampControl = lampControl;
    this.analysis = analysis;
    this.zonesForFiltering = new ArrayList<>( 2 );
    this.zonesToSwitchOn = new ArrayList<>( 2 );
    this.cookingAreaTimeout = new Timeout();
    this.diningAreaTimeout = new Timeout();
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    setTimeouts();
    selectZoneLampsSwitchMode();
    switchZoneLamps();
    cleanup();
  }

  private void setTimeouts() {
    cookingAreaTimeout.setIf( isCookingAreaHot() );
    diningAreaTimeout.setIf( isDiningAreaHot() || !isCookingAreaHot() || isCookingAreaHot() && isDiningAreaUsedALot() );
  }

  private boolean isDiningAreaHot() {
    return analysis.isZoneActivated( DINING_AREA );
  }

  private boolean isDiningAreaUsedALot() {
    return analysis.isAllocationStatusAtLeast( DINING_AREA, DINING_AREA_ALLOCATION_THRESHOLD );
  }

  private boolean isCookingAreaHot() {
    return    analysis.isZoneActivated( COOKING_AREA ) && !analysis.isAdjacentZoneActivated( COOKING_AREA )
           || isCookingAreaUsedALot();
  }

  private void selectZoneLampsSwitchMode() {
    cookingAreaTimeout.executeIfNotExpired( () -> selectLampSwitchMode( COOKING_AREA, isCookingAreaUsedALot() ) );
    diningAreaTimeout.executeIfNotExpired( () -> selectLampSwitchMode( DINING_AREA, isDiningAreaActivivelyUsed() ) );
  }

  private void selectLampSwitchMode( SectionDefinition sectionDefinition, boolean condition ) {
    if( condition ) {
      zonesToSwitchOn.add( sectionDefinition );
    } else {
      zonesForFiltering.add( sectionDefinition );
    }
  }

  private boolean isCookingAreaUsedALot() {
    return analysis.isAllocationStatusAtLeast( COOKING_AREA, COOKING_AREA_ALLOCATION_THRESHOLD );
  }

  private boolean isDiningAreaActivivelyUsed() {
    return    analysis.isOverallActivityStatusAtLeast( DINING_AREA_OVERALL_ACTIVITY_THRESHOLD )
          && isDiningAreaUsedALot();
  }

  private void switchZoneLamps() {
    if( !zonesToSwitchOn.isEmpty() ) {
      lampControl.switchOnZoneLamps( zonesToSwitchOn.stream().toArray( SectionDefinition[]::new ) );
    }
    if( !zonesForFiltering.isEmpty() ) {
      lampControl.setZoneLampsForFiltering( zonesForFiltering.stream().toArray( SectionDefinition[]::new ) );
    }
  }

  private void cleanup() {
    zonesToSwitchOn.clear();
    zonesForFiltering.clear();
  }
}