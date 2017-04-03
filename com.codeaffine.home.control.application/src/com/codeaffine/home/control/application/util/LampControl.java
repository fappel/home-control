package com.codeaffine.home.control.application.util;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Stream;

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

  public void switchOnZoneLamps( SectionDefinition ... zoneDefinitions ) {
    verifyNotNull( zoneDefinitions, "zoneDefinitionsa" );

    lampSwitchOperation.setLampsToSwitchOn( collectZoneLampDefinitions( zoneDefinitions ) );
  }

  public void switchOffZoneLamps( SectionDefinition ... zoneDefinitions ) {
    verifyNotNull( zoneDefinitions, "zoneDefinitions" );

    lampSwitchOperation.setLampsToSwitchOff( collectZoneLampDefinitions( zoneDefinitions ) );
  }

  public void provideZoneLampsForFiltering( SectionDefinition ... zoneDefinitions ) {
    verifyNotNull( zoneDefinitions, "zoneDefinitions" );

    lampSwitchOperation.addFilterableLamps( collectZoneLampDefinitions( zoneDefinitions ) );
  }

  private LampDefinition[] collectZoneLampDefinitions( SectionDefinition ... definitions ) {
    return toDefinitions( Stream.of( definitions )
      .flatMap( definition -> lampCollector.collectZoneLamps( definition ).stream() )
      .collect( toSet() ) );
  }

  private static LampDefinition[] toDefinitions( Set<Lamp> lamps ) {
    return lamps.stream().map( lamp -> lamp.getDefinition() ).toArray( LampDefinition[]::new );
  }
}