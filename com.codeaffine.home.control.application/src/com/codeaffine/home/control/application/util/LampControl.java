package com.codeaffine.home.control.application.util;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.operation.LampCollector;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;

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
    verifyNotNull( zoneDefinitions, "zoneDefinitions" );

    switchOnLamps( collectZoneLampDefinitions( zoneDefinitions ) );
  }

  public void switchOffZoneLamps( SectionDefinition ... zoneDefinitions ) {
    verifyNotNull( zoneDefinitions, "zoneDefinitions" );

    switchOffLamps( collectZoneLampDefinitions( zoneDefinitions ) );
  }

  public void setZoneLampsForFiltering( SectionDefinition ... zoneDefinitions ) {
    verifyNotNull( zoneDefinitions, "zoneDefinitions" );

    setLampsForFiltering( collectZoneLampDefinitions( zoneDefinitions ) );
  }

  public void switchOnLamps( LampDefinition ... lampDefinitions ) {
    verifyNotNull( lampDefinitions, "lampDefinitions" );

    lampSwitchOperation.setLampsToSwitchOn( lampDefinitions );
  }

  public void switchOffLamps( LampDefinition ... lampDefinitions ) {
    verifyNotNull( lampDefinitions, "lampDefinitions" );

    lampSwitchOperation.setLampsToSwitchOff( lampDefinitions );
  }

  public void setLampsForFiltering( LampDefinition ... lampDefinitions ) {
    verifyNotNull( lampDefinitions, "lampDefinitions" );

    lampSwitchOperation.addFilterableLamps( lampDefinitions );
  }

  public static LampDefinition[] toDefinitions( Set<Lamp> lamps ) {
    verifyNotNull( lamps, "lamps" );

    return lamps.stream().map( lamp -> lamp.getDefinition() ).toArray( LampDefinition[]::new );
  }

  private LampDefinition[] collectZoneLampDefinitions( SectionDefinition ... definitions ) {
    return toDefinitions( Stream.of( definitions )
      .flatMap( definition -> lampCollector.collectZoneLamps( definition ).stream() )
      .collect( toSet() ) );
  }
}