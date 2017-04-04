package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.util.ActivityStatus.*;
import static com.codeaffine.home.control.application.util.AllocationStatus.*;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.util.AllocationStatus;
import com.codeaffine.home.control.application.util.Analysis;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;

public class BedroomScene implements Scene {

  private final SunPositionProvider sunPositionProvider;
  private final LampControl lampControl;
  private final Timeout actionTimeout;
  private final Timeout bedTimeout;
  private final Analysis analysis;

  public BedroomScene( LampControl lampControl, Analysis analysis, SunPositionProvider sunPositionProvider ) {
    this.lampControl = lampControl;
    this.analysis = analysis;
    this.sunPositionProvider = sunPositionProvider;
    this.actionTimeout = new Timeout( 20L, SECONDS );
    this.bedTimeout = new Timeout( 20L, SECONDS );
  }

  @Override
  public void prepare( Scene previous ) {
    bedTimeout.setIf( analysis.isZoneActivated( DRESSING_AREA ) || analysis.isActivityStatusAtLeast( BED, AROUSED ) );
    actionTimeout.setIf(    !bedTimeout.isExpired()
                         && (    analysis.isAllocationStatusAtLeast( DRESSING_AREA, getDressingAreaAllocationMinimum() )
                              || analysis.isActivityStatusAtLeast( BED, LIVELY ) ) );
    if( bedTimeout.isExpired() ) {
      lampControl.switchOffZoneLamps( BED, DRESSING_AREA );
    }
    if( !actionTimeout.isExpired() ) {
      lampControl.switchOnZoneLamps( BED, DRESSING_AREA );
    }
  }

  private AllocationStatus getDressingAreaAllocationMinimum() {
    AllocationStatus nightStatus = analysis.isOverallActivityStatusAtLeast( AROUSED ) ? OCCASIONAL : FREQUENT;
    return sunPositionProvider.getStatus().getZenit() > 0 ? CONTINUAL : nightStatus;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}