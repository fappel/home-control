package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.ActivityStatus.*;
import static com.codeaffine.home.control.status.util.AllocationStatus.*;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.status.util.ActivityStatus;
import com.codeaffine.home.control.status.util.AllocationStatus;
import com.codeaffine.home.control.status.util.Analysis;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;

public class BedroomScene implements Scene {

  private final SunPositionSupplier sunPositionSupplier;
  private final LampControl lampControl;
  private final Timeout actionTimeout;
  private final Timeout bedTimeout;
  private final Analysis analysis;

  public BedroomScene( LampControl lampControl, Analysis analysis, SunPositionSupplier sunPositionSupplier ) {
    this.lampControl = lampControl;
    this.analysis = analysis;
    this.sunPositionSupplier = sunPositionSupplier;
    this.actionTimeout = new Timeout( 20L, SECONDS );
    this.bedTimeout = new Timeout( 20L, SECONDS );
  }

  @Override
  public void prepare( Scene previous ) {
    bedTimeout.setIf(    analysis.isZoneActivated( DRESSING_AREA )
                      || analysis.isActivityStatusAtLeast( BED, getBedActivityMinimum() ) );
    actionTimeout.setIf(    !bedTimeout.isExpired()
                         && (    analysis.isAllocationStatusAtLeast( DRESSING_AREA, getDressingAreaAllocationMinimum() )
                              || analysis.isActivityStatusAtLeast( BED, getBedActivityMinimum() ) ) );
    bedTimeout.executeIfNotExpired( () -> lampControl.switchOnZoneLamps( BED, DRESSING_AREA ) );
    if( bedTimeout.isExpired() || analysis.isAllocationStatusAtLeast( BED, PERMANENT ) ) {
      lampControl.switchOffZoneLamps( BED, DRESSING_AREA );
    }

  }

  private ActivityStatus getBedActivityMinimum() {
    return sunPositionSupplier.getStatus().getZenit() > 0 ? AROUSED : LIVELY;
  }

  private AllocationStatus getDressingAreaAllocationMinimum() {
    AllocationStatus nightStatus = analysis.isOverallActivityStatusAtLeast( AROUSED ) ? OCCASIONAL : FREQUENT;
    return sunPositionSupplier.getStatus().getZenit() > 0 ? CONTINUAL : nightStatus;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}