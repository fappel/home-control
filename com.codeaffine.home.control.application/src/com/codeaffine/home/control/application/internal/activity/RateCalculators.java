package com.codeaffine.home.control.application.internal.activity;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

class RateCalculators {

  static Map<Section, ActivityRateCalculator> createActivityCalculators(
    EntityRegistry entityRegistry, long timeFrame, long duration )
  {
    return streamOfRelatedSections( entityRegistry )
      .collect( toMap( section -> section, section -> newActivityCalculator( section, timeFrame, duration ) ) );
  }

  static Map<Section, AllocationRateCalculator> createAllocationCalculators(
    EntityRegistry entityRegistry, ActivationProvider activationProvider, long timeFrame, long duration )
  {
    return streamOfRelatedSections( entityRegistry )
      .collect( toMap( section -> section,
                       section -> newAllocationCalculator( section, activationProvider, timeFrame, duration ) ) );
  }

  private static Stream<Section> streamOfRelatedSections( EntityRegistry entityRegistry ) {
    return entityRegistry
      .findByDefinitionType( SectionDefinition.class )
      .stream()
      .filter( section -> !section.getChildren( ActivationSensorDefinition.class ).isEmpty() );
  }

  private static ActivityRateCalculator newActivityCalculator( Section section, long timeFrame, long duration ) {
    return new ActivityRateCalculator( section, newActivationTracker( timeFrame, duration ) );
  }

  private static AllocationRateCalculator newAllocationCalculator(
    Section section, ActivationProvider activationProvider, long timeFrame, long duration )
  {
    return new AllocationRateCalculator( section, activationProvider, newActivationTracker( timeFrame, duration ) );
  }

  private static ActivationTracker newActivationTracker( long timeFrame, long intervalDuration ) {
    return new ActivationTracker( timeFrame, intervalDuration );
  }
}