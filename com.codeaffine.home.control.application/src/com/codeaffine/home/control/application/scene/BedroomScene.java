package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.ActivityStatus.*;
import static com.codeaffine.home.control.status.util.AllocationStatus.*;
import static com.codeaffine.home.control.status.util.SunLightStatus.NIGHT;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.util.ActivityStatus;
import com.codeaffine.home.control.status.util.AllocationStatus;
import com.codeaffine.home.control.status.util.Analysis;

public class BedroomScene implements Scene {

  private final LampControl lampControl;
  private final Timeout actionTimeout;
  private final Timeout bedTimeout;
  private final Analysis analysis;

  public BedroomScene( LampControl lampControl, Analysis analysis ) {
    this.lampControl = lampControl;
    this.analysis = analysis;
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
    return analysis.isSunLightStatusAtMost( NIGHT ) ? LIVELY : AROUSED;
  }

  private AllocationStatus getDressingAreaAllocationMinimum() {
    return analysis.isSunLightStatusAtMost( NIGHT ) ? FREQUENT : CONTINUAL;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}