package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.OnOff.ON;
import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Collection;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.status.ComputerStatusProvider;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;


public class DayScene implements Scene {

  private static final Collection<LampDefinition> DAY_LAMPS
    = asList( SinkUplight,
              KitchenCeiling,
              FanLight1,
              FanLight2,
              BedRoomCeiling,
              BedStand,
              BathRoomCeiling,
              HallCeiling );

  private final ComputerStatusProvider computerStatusProvider;
  private final LampSwitchOperation lampSwitchOperation;
  private final ActivationProvider activationProvider;
  private final ActivityProvider activityProvider;

  private final Logger logger;

  public DayScene( ActivationProvider activationProvider,
                   LampSwitchOperation lampSwitchOperation,
                   ActivityProvider activityProvider,
                   ComputerStatusProvider computerStatusProvider,
                   Logger logger )
  {
    this.computerStatusProvider = computerStatusProvider;
    this.lampSwitchOperation = lampSwitchOperation;
    this.activationProvider = activationProvider;
    this.activityProvider = activityProvider;
    this.logger = logger;
  }

  @Override
  public void prepare() {
    lampSwitchOperation.setLampFilter( lamp -> DAY_LAMPS.contains( lamp.getDefinition() ) );
    activationProvider.getStatus().getZone( LIVING_AREA ).ifPresent( zone -> configureWorkAreaAdditions() );
    activationProvider.getStatus().getZone( WORK_AREA ).ifPresent( zone -> configureWorkAreaAdditions( zone ) );
  }

  private void configureWorkAreaAdditions( Zone zone ) {
    if( zone.isAdjacentActivated() ) {
      configureWorkAreaAdditions();
    }
  }

  private void configureWorkAreaAdditions() {
    Activity activity = activityProvider.getStatus();
    BigDecimal rate = new BigDecimal( 0 );
    if( activity.getOverallActivity().intValue() > 0 ) {
      rate = new BigDecimal( activity.getSectionActivity( WORK_AREA ).get().intValue() )
          .divide( new BigDecimal( activity.getOverallActivity().intValue() ), 2, BigDecimal.ROUND_HALF_UP );
    }
    logger.info( "rate: " + rate );
    if( rate.compareTo( new BigDecimal( 0.5 ) ) > 0 || computerStatusProvider.getStatus() == ON ) {
      lampSwitchOperation.setLampsToSwitchOn( DeskUplight, ChimneyUplight );
    } else {
      lampSwitchOperation.setLampsToSwitchOn( FanLight1, FanLight2 );
    }
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }
}
