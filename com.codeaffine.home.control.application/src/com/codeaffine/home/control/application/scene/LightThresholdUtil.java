package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.status.type.OnOff.OFF;
import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
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
    Map<LightSensorDefinition, Set<Collection<LampDefinition>>> sensorToLampsMapping = getSensorToLampsMapping();
    Set<LampDefinition> potentialSwitchOnLamps = collectPotentialLampsToSwitchOn( sensorToLampsMapping );
    Set<LampDefinition> lampsToSwitchOff = collectSwitchedOffLampsAboveSwitchOnThreshold( sensorToLampsMapping );
    lampsToSwitchOff.addAll( collectLampsAboveSwitchOffThreshold( sensorToLampsMapping ) );
    lampsToSwitchOff.removeAll( potentialSwitchOnLamps );
    return lampsToSwitchOff.stream().toArray( LampDefinition[]::new );
  }

  private Map<LightSensorDefinition, Set<Collection<LampDefinition>>> getSensorToLampsMapping() {
    return Stream.of( SectionDefinition.values() )
      .filter( sectionDefinition -> !getSensorsOfSection( sectionDefinition ).isEmpty() )
      .collect( groupingBy( sectionDefinition -> getSensorsOfSection( sectionDefinition ).stream().findFirst().get(),
                            mapping( sectionDefinition -> getLampsOfSection( sectionDefinition ), toSet() ) ) );
  }

  private Collection<LightSensorDefinition> getSensorsOfSection( SectionDefinition sectionDefinition ) {
    return relationProvider.getChildren( sectionDefinition, LightSensorDefinition.class );
  }

  private Collection<LampDefinition> getLampsOfSection( SectionDefinition sectionDefinition ) {
    return relationProvider.getChildren( sectionDefinition, LampDefinition.class );
  }

  private Set<LampDefinition> collectPotentialLampsToSwitchOn(
    Map<LightSensorDefinition, Set<Collection<LampDefinition>>> sensorToLampsMapping )
  {
    return sensorToLampsMapping
        .keySet()
        .stream()
        .filter( sensorDefinition -> isBelowSwitchOnThreshold( sensorDefinition ) )
        .flatMap( sensorDefinition -> sensorToLampsMapping.get( sensorDefinition ).stream() )
        .flatMap( sectionLamps -> sectionLamps.stream() )
        .collect( toSet() );
  }

  private boolean isBelowSwitchOnThreshold( LightSensorDefinition sensorDefinition ) {
    return compareLightValueToThreshold( sensorDefinition, () -> preference.getSwitchOnThreshold() ) <= 0;
  }

  private Set<LampDefinition> collectSwitchedOffLampsAboveSwitchOnThreshold(
    Map<LightSensorDefinition, Set<Collection<LampDefinition>>> sensorToLampsMapping )
  {
    return sensorToLampsMapping
        .keySet()
        .stream()
        .filter( sensorDefinition -> isAboveSwitchOnThreshold( sensorDefinition ) )
        .flatMap( sensorDefinition -> sensorToLampsMapping.get( sensorDefinition ).stream() )
        .flatMap( sectionLamps -> sectionLamps.stream() )
        .filter( lamp -> relationProvider.findByDefinition( lamp ).getOnOffStatus() == OFF )
        .collect( toSet() );
  }

  private boolean isAboveSwitchOnThreshold( LightSensorDefinition sensorDefinition ) {
    return compareLightValueToThreshold( sensorDefinition, () -> preference.getSwitchOnThreshold() ) > 0;
  }

  private Set<LampDefinition> collectLampsAboveSwitchOffThreshold(
    Map<LightSensorDefinition, Set<Collection<LampDefinition>>> sensorToLampsMapping )
  {
    return sensorToLampsMapping
        .keySet()
        .stream()
        .filter( sensorDefinition -> isAboveSwitchOffThreshold( sensorDefinition ) )
        .flatMap( sensorDefinition -> sensorToLampsMapping.get( sensorDefinition ).stream() )
        .flatMap( sectionLamps -> sectionLamps.stream() )
        .collect( toSet() );
  }

  private boolean isAboveSwitchOffThreshold( LightSensorDefinition sensorDefinition ) {
    return compareLightValueToThreshold( sensorDefinition, () -> preference.getSwitchOffThreshold() ) > 0;
  }

  private int compareLightValueToThreshold(
    LightSensorDefinition sensorDefinition, Supplier<Map<LightSensorDefinition, Integer>> thresholdSupplier )
  {
    Integer lightValue = relationProvider.findByDefinition( sensorDefinition ).getLightValue();
    Map<LightSensorDefinition, Integer> thresholds = thresholdSupplier.get();
    return lightValue.compareTo( thresholds.get( sensorDefinition  ) );
  }
}