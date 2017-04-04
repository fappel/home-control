package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.util.ActivityStatus.LIVELY;
import static com.codeaffine.home.control.application.util.AllocationStatus.FREQUENT;

import com.codeaffine.home.control.application.util.Analysis;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
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
        || analysis.isAllocationStatusAtLeast( COOKING_AREA, FREQUENT ) )
    {
      cookingAreaTimeout.set();
      if( analysis.isAllocationStatusAtLeast( DINING_AREA, FREQUENT ) ) {
        diningAreaTimeout.set();
      }
    } else {
      diningAreaTimeout.set();
    }

    if( analysis.isZoneActivated( DINING_AREA ) ) {
      diningAreaTimeout.set();
    }

    if( !cookingAreaTimeout.isExpired() ) {
      if( analysis.isAllocationStatusAtLeast( COOKING_AREA, FREQUENT ) ) {
        lampControl.switchOnZoneLamps( COOKING_AREA );
      } else {
        lampControl.setZoneLampsForFiltering( COOKING_AREA );
      }
    }
    if( !diningAreaTimeout.isExpired() ) {
      if(    analysis.isOverallActivityStatusAtLeast( LIVELY )
          && analysis.isAllocationStatusAtLeast( DINING_AREA, FREQUENT ) )
      {
        lampControl.switchOnZoneLamps( DINING_AREA );
      } else {
        lampControl.setZoneLampsForFiltering( DINING_AREA );
      }
    }
  }
}