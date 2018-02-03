package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BedStand;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.ActivityStatus.LIVELY;
import static com.codeaffine.home.control.status.util.AllocationStatus.FREQUENT;
import static com.codeaffine.home.control.status.util.SunLightStatus.NIGHT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

import java.util.Set;

import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.util.Analysis;

public class BedroomScene implements Scene {

  private final LampControl lampControl;
  private final Timeout bedRoomTimeout;
  private final Analysis analysis;

  public BedroomScene( LampControl lampControl, Analysis analysis, BedroomScenePreference preference ) {
    this.lampControl = lampControl;
    this.analysis = analysis;
    this.bedRoomTimeout = new Timeout( preference );
  }

  @Override
  public void prepare() {
    bedRoomTimeout.setIf( isBedroomHot() );
    bedRoomTimeout.executeIfNotExpired( () -> switchLampsOn() );
    bedRoomTimeout.executeIfExpired( () -> switchLampsOff() );
  }

  private boolean isBedroomHot() {
    return collectBedroomActivations().size() > 1 || analysis.isZoneActivated( DRESSING_AREA );
  }

  private Set<Zone> collectBedroomActivations() {
    return analysis
      .getActivatedZones()
      .stream()
      .filter( zone -> asList( BED, BED_SIDE, DRESSING_AREA ).contains( zone.getZoneEntity().getDefinition() ) )
      .collect( toSet() );
  }

  private void switchLampsOn() {
    lampControl.setZoneLampsForFiltering( BED, BED_SIDE, DRESSING_AREA );
    complementBedAreaLightingIfNeeded();
    complementDressingAreaLightingIfNeeded();
  }

  private void complementBedAreaLightingIfNeeded() {
    if( isBedAreaHot() ) {
      lampControl.switchOnLamps( BedStand );
      if( analysis.isActivityStatusAtLeast( BED_SIDE, LIVELY ) ) {
        lampControl.switchOnZoneLamps( BED_SIDE );
      }
    }
  }

  private boolean isBedAreaHot() {
    return analysis.isZoneActivated( BED_SIDE ) || analysis.isZoneActivated( BED );
  }

  private void complementDressingAreaLightingIfNeeded() {
    if( isDressingAreaUsedALot() ) {
      lampControl.switchOnZoneLamps( DRESSING_AREA );
    }
  }

  private boolean isDressingAreaUsedALot() {
    return analysis.isSunLightStatusAtMost( NIGHT ) && analysis.isAllocationStatusAtLeast( DRESSING_AREA, FREQUENT );
  }

  private void switchLampsOff() {
    lampControl.switchOffZoneLamps( BED, BED_SIDE, DRESSING_AREA );
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}