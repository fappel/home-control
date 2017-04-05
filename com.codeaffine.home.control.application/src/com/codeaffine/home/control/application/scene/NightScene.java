package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampSelectionStrategy.ALL;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.values;
import static com.codeaffine.home.control.status.util.ActivityStatus.BRISK;
import static com.codeaffine.home.control.status.util.MotionStatus.FOCUSSED;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.util.Analysis;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.Scene;

public class NightScene implements Scene {

  private static final Collection<LampDefinition> NIGHT_LAMPS
    = asList( SinkUplight,
              ChimneyUplight, WindowUplight,
              BedStand,
              BathRoomCeiling,
              HallCeiling );

  private final LampSwitchOperation lampSwitchOperation;
  private final Timeout allOnTimeout;
  private final Analysis analysis;

  public NightScene( Analysis analysis, LampSwitchOperation lampSwitchOperation ) {
    this.analysis = analysis;
    this.lampSwitchOperation = lampSwitchOperation;
    this.allOnTimeout = new Timeout( 2L, MINUTES );
  }

  @Override
  public void prepare() {
    lampSwitchOperation.setLampFilter( lamp -> NIGHT_LAMPS.contains( lamp.getDefinition() ) );
    allOnTimeout.setIf(    allOnTimeout.isExpired()
                        && Stream.of( values() ).anyMatch( section -> analysis.isMotionStatusAtLeast( section, FOCUSSED ) )
                        && analysis.isOverallActivityStatusAtMost( BRISK ) );
    if( !allOnTimeout.isExpired() ) {
      lampSwitchOperation.setLampSelectionStrategy( ALL );
    }
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}