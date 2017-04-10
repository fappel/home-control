package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSelectionStrategy.ALL;
import static com.codeaffine.home.control.application.operation.LampTimeoutModus.ON;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.util.ActivityStatus.BRISK;
import static com.codeaffine.home.control.status.util.MotionStatus.FOCUSSED;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.util.Analysis;

public class NightScene implements Scene {

  private static final Collection<LampDefinition> NIGHT_LAMPS
    = asList( SinkUplight,
              ChimneyUplight, WindowUplight,
              BedStand,
              BathRoomCeiling,
              HallCeiling );

  private final LampControl lampControl;
  private final Timeout allOnTimeout;
  private final Analysis analysis;

  NightScene( Analysis analysis, LampControl lampControl ) {
    this.allOnTimeout = new Timeout( 2L, MINUTES );
    this.lampControl = lampControl;
    this.analysis = analysis;
  }

  @Override
  public void prepare() {
    lampControl.setLampTimeoutModus( ON );

    lampControl.setLampFilter( lamp -> NIGHT_LAMPS.contains( lamp.getDefinition() ) );
    allOnTimeout.setIf(    allOnTimeout.isExpired()
                        && Stream.of( SectionDefinition.values() )
                             .anyMatch( section -> analysis.isMotionStatusAtLeast( section, FOCUSSED ) )
                        && analysis.isOverallActivityStatusAtMost( BRISK ) );
    if( !allOnTimeout.isExpired() ) {
      lampControl.setLampSelectionStrategy( ALL );
      lampControl.addGroupOfRelatedSections( SectionDefinition.values() );
    } else {
      lampControl.addGroupOfRelatedSections( LIVING_AREA, WORK_AREA );
      lampControl.addGroupOfRelatedSections( BED, BED_SIDE, DRESSING_AREA );
      lampControl.addGroupOfRelatedSections( DINING_AREA, COOKING_AREA );
    }
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}