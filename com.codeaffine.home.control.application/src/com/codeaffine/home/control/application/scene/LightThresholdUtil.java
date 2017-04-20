package com.codeaffine.home.control.application.scene;

import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;

public class LightThresholdUtil {

  private final EntityRelationProvider relationProvider;
  private final LightThresholdPreference preference;

  LightThresholdUtil( EntityRelationProvider relationProvider, LightThresholdPreference preference ) {
    this.relationProvider = relationProvider;
    this.preference = preference;
  }

  LampDefinition[] collectLampsOfZonesWithEnoughDayLight() {
    Map<LightSensorDefinition, Set<Collection<LampDefinition>>> collect = Stream.of( SectionDefinition.values() )
      .filter( sectionDefinition -> !relationProvider.getChildren( sectionDefinition, LightSensorDefinition.class ).isEmpty() )
      .collect( groupingBy( sectionDefinition -> relationProvider.getChildren( sectionDefinition, LightSensorDefinition.class ).stream().findFirst().get(),
                            mapping( sectionDefinition -> relationProvider.getChildren( sectionDefinition, LampDefinition.class ),
                                     toSet() ) ) );
    Set<LampDefinition> potentialSwitchOnLamps = collect
      .keySet()
      .stream()
      .filter( sensorDefinition -> isBelowLightThreshold( sensorDefinition ) )
      .flatMap( sensorDefinition -> collect.get( sensorDefinition ).stream() )
      .flatMap( sectionLamps -> sectionLamps.stream() )
      .collect( toSet() );
    Set<LampDefinition> lampsToSwitchOff = collect
      .keySet()
      .stream()
      .filter( sensorDefinition -> isAboveLightThreshold( sensorDefinition ) )
      .flatMap( sensorDefinition -> collect.get( sensorDefinition ).stream() )
      .flatMap( sectionLamps -> sectionLamps.stream() )
      .collect( toSet() );
    lampsToSwitchOff.removeAll( potentialSwitchOnLamps );
    return lampsToSwitchOff.stream().toArray( LampDefinition[]::new );
  }

  private boolean isAboveLightThreshold( LightSensorDefinition sensorDefinition ) {
    Integer lightValue = relationProvider.findByDefinition( sensorDefinition ).getLightValue();
    int threshold = preference.getThreshold().get( sensorDefinition  ).intValue();
    return lightValue.intValue() > threshold;
  }

  private boolean isBelowLightThreshold( LightSensorDefinition sensorDefinition ) {
    Integer lightValue = relationProvider.findByDefinition( sensorDefinition ).getLightValue();
    int threshold = preference.getThreshold().get( sensorDefinition  ).intValue();
    return lightValue.intValue() <= threshold;
  }
}