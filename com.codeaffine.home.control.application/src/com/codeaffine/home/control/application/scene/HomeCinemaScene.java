package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static java.util.stream.Collectors.toSet;

import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.status.Scene;

public class HomeCinemaScene implements Scene {

  private final ZoneActivationProvider zoneActivationProvider;
  private final LampSwitchOperation lampSwitchOperation;

  public HomeCinemaScene( LampSwitchOperation lampSwitchOperation, ZoneActivationProvider zoneActivationProvider ) {
    this.zoneActivationProvider = zoneActivationProvider;
    this.lampSwitchOperation = lampSwitchOperation;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public void prepare() {
    if( isLivingAreaActive() ) {
      lampSwitchOperation.setLampsToSwitchOn( ChimneyUplight, WindowUplight );
      lampSwitchOperation.setLampsToSwitchOff( DeskUplight, FanLight1, FanLight2 );
    }
  }

  private boolean isLivingAreaActive() {
    return !zoneActivationProvider
      .getStatus()
      .stream()
      .map( activation -> activation.getZone() )
      .filter( section -> section.getDefinition().equals( SectionDefinition.LIVING_AREA ) )
      .collect( toSet() )
      .isEmpty();
  }
}
