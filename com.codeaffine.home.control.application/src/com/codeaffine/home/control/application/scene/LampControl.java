package com.codeaffine.home.control.application.scene;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Set;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampCollector;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;

public class LampControl {

  private final LampSwitchOperation lampSwitchOperation;
  private final LampCollector lampCollector;

  public LampControl( LampSwitchOperation lampSwitchOperation, LampCollector lampCollector ) {
    verifyNotNull( lampSwitchOperation, "lampSwitchOperation" );
    verifyNotNull( lampCollector, "lampCollector" );

    this.lampSwitchOperation = lampSwitchOperation;
    this.lampCollector = lampCollector;
  }

  void switchOnZoneLamps( SectionDefinition zoneDefinition ) {
    verifyNotNull( zoneDefinition, "zoneDefinition" );

    lampSwitchOperation.setLampsToSwitchOn( collectZoneLampDefinitions( zoneDefinition ) );
  }

  void provideZoneLampsForFiltering( SectionDefinition zoneDefinition ) {
    verifyNotNull( zoneDefinition, "zoneDefinition" );

    LampDefinition[] filterableLamps = collectZoneLampDefinitions( zoneDefinition );
    lampSwitchOperation.addFilterableLamps( filterableLamps );
  }

  private LampDefinition[] collectZoneLampDefinitions( SectionDefinition zoneDefinition ) {
    return toDefinitions( lampCollector.collectZoneLamps( zoneDefinition ) );
  }

  private static LampDefinition[] toDefinitions( Set<Lamp> lamps ) {
    return lamps.stream().map( lamp -> lamp.getDefinition() ).toArray( LampDefinition[]::new );
  }
}