package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;

import java.math.BigDecimal;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampCollector;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.status.Scene;

public class KitchenScene implements Scene {

  private final LampSwitchOperation lampSwitchOperation;
  private final ActivationProvider activationProvider;
  private final ActivityProvider activityProvider;
  private final LampCollector lampCollector;

  public KitchenScene( LampSwitchOperation lampSwitchOperation,
                       ActivationProvider activationProvider,
                       ActivityProvider activityProvider,
                       LampCollector lampCollector )
  {
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
    Activation status = activationProvider.getStatus();
    if( !status.getZone( COOKING_AREA ).isPresent() || status.getZone( COOKING_AREA ).get().isAdjacentActivated() ) {
      configureCookingAreaAdditions();
    } else {
      setLampsToSwitchOn( COOKING_AREA );
    }
  }

  private void configureCookingAreaAdditions() {
    if( computeCookingAreaActivity() > 0.5 ) {
      setLampsToSwitchOn( COOKING_AREA );
    } else {
      activateAllLampsForFiltering( DINING_AREA );
    }
  }

  private double computeCookingAreaActivity() {
    Activity activity = activityProvider.getStatus();
    BigDecimal result = new BigDecimal( 0 );
    if( activity.getOverallActivity().intValue() > 0 ) {
      result = new BigDecimal( activity.getSectionActivity( COOKING_AREA ).get().intValue() )
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