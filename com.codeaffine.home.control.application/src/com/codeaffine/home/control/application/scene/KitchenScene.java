package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.analysis.Analysis.ActivityStatus.AROUSED;
import static com.codeaffine.home.control.application.analysis.Analysis.AllocationStatus.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;

import com.codeaffine.home.control.application.analysis.Analysis;
import com.codeaffine.home.control.status.Scene;

public class KitchenScene implements Scene {

  private final Timeout cookingAreaTimeout;
  private final Timeout diningAreaTimeout;
  private final LampControl lampControl;
  private final Analysis analysis;

  public KitchenScene( LampControl lampControl, Analysis analysis ) {
    this.lampControl = lampControl;
    this.analysis = analysis;
    this.cookingAreaTimeout = new Timeout();
    this.diningAreaTimeout = new Timeout();
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    if(    analysis.isZoneActivated( COOKING_AREA ) && !analysis.isAdjacentZoneActivated( COOKING_AREA )
        || analysis.isZoneAllocationAtLeast( COOKING_AREA, FREQUENTLY ) )
    {
      cookingAreaTimeout.set();
      if( analysis.isZoneAllocationAtLeast( DINING_AREA, FREQUENTLY ) ) {
        diningAreaTimeout.set();
      }
    } else {
      diningAreaTimeout.set();
    }

    if( analysis.isZoneActivated( DINING_AREA ) ) {
      diningAreaTimeout.set();
    }

    if( !cookingAreaTimeout.isExpired() ) {
      if( analysis.isZoneAllocationAtLeast( COOKING_AREA, FREQUENTLY ) ) {
        lampControl.switchOnZoneLamps( COOKING_AREA );
      } else {
        lampControl.provideZoneLampsForFiltering( COOKING_AREA );
      }
    }
    if( !diningAreaTimeout.isExpired() ) {
      if(    analysis.isOverallActivityAtLeast( AROUSED )
          && analysis.isZoneAllocationAtLeast( DINING_AREA, FREQUENTLY ) )
      {
        lampControl.switchOnZoneLamps( DINING_AREA );
      } else {
        lampControl.provideZoneLampsForFiltering( DINING_AREA );
      }
    }
  }
}