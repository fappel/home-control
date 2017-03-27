package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.type.OnOff.ON;

import java.math.BigDecimal;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampCollector;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.status.ComputerStatusProvider;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.status.Scene;

public class LivingRoomScene implements Scene {

  private final ComputerStatusProvider computerStatusProvider;
  private final LampSwitchOperation lampSwitchOperation;
  private final ActivationProvider activationProvider;
  private final ActivityProvider activityProvider;
  private final LampCollector lampCollector;
  private final ActivityMath activityMath;

  public LivingRoomScene( ComputerStatusProvider computerStatusProvider,
                          LampSwitchOperation lampSwitchOperation,
                          ActivationProvider activationProvider,
                          ActivityProvider activityProvider,
                          LampCollector lampCollector )
  {
    this.activityMath = new ActivityMath( activityProvider, activationProvider );
    this.computerStatusProvider = computerStatusProvider;
    this.lampSwitchOperation = lampSwitchOperation;
    this.activationProvider = activationProvider;
    this.activityProvider = activityProvider;
    this.lampCollector = lampCollector;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    Activation activation = activationProvider.getStatus();
    if( !activation.getZone( WORK_AREA ).isPresent() || activation.getZone( WORK_AREA ).get().isAdjacentActivated() ) {
      configureWorkAreaAdditions();
    } else {
      setLampsToSwitchOn( WORK_AREA );
    }
  }

  private void configureWorkAreaAdditions() {
    Activity activity = activityProvider.getStatus();

    Percent maximum = activityMath.calculateMaximumOfPathActivityFor( WORK_AREA ).get();
    Percent geometric = activityMath.calculateGeometricMeanOfPathActivityFor( WORK_AREA ).get();
    Percent arithmetic = activityMath.calculateArithmeticMeanOfPathActivityFor( WORK_AREA ).get();
    int delta = arithmetic.intValue() - geometric.intValue();
    if(    activity.getOverallActivity().compareTo( Percent.P_010 ) > 0
        && (    ( activity.getSectionActivity( WORK_AREA ).get().compareTo( maximum ) == 0 && delta > 10 )
             || computeWorkAreaActivity() > 0.5 )
        || computerStatusProvider.getStatus() == ON )
    {

    }


    if( computeWorkAreaActivity() > 0.5 || computerStatusProvider.getStatus() == ON ) {
      setLampsToSwitchOn( WORK_AREA );
    } else {
      activateAllLampsForFiltering( LIVING_AREA );
    }
  }

  private double computeWorkAreaActivity() {
    Activity activity = activityProvider.getStatus();
    BigDecimal result = new BigDecimal( 0 );
    if( activity.getOverallActivity().intValue() > 0 ) {
      result = new BigDecimal( activity.getSectionActivity( WORK_AREA ).get().intValue() )
          .divide( new BigDecimal( activity.getOverallActivity().intValue() ), 2, BigDecimal.ROUND_HALF_UP );
    }
    return result.doubleValue();
  }

  private void setLampsToSwitchOn( SectionDefinition zoneDefinition ) {
    lampSwitchOperation.setLampsToSwitchOn( collectAllLampDefinitions( zoneDefinition ) );
  }

  private void activateAllLampsForFiltering( SectionDefinition zoneDefinition ) {
    LampDefinition[] filterableLamps = collectAllLampDefinitions( zoneDefinition );
    lampSwitchOperation.addFilterableLamps( filterableLamps );
  }

  private LampDefinition[] collectAllLampDefinitions( SectionDefinition zoneDefinition ) {
    return lampCollector
      .collectZoneLamps( zoneDefinition )
      .stream()
      .map( lamp -> lamp.getDefinition() )
      .toArray( LampDefinition[]::new );
  }
}