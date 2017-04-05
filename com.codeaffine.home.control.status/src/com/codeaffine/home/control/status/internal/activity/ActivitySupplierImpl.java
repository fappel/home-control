package com.codeaffine.home.control.status.internal.activity;

import static com.codeaffine.home.control.status.internal.activity.Messages.INFO_ACTIVITY_RATE;
import static com.codeaffine.home.control.status.internal.activity.RateCalculators.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.Boolean.*;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusSupplierCore;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.Activity;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.type.Percent;

public class ActivitySupplierImpl extends RateCalculator implements ActivitySupplier {

  static final long OBSERVATION_TIME = 3L; // Minutes
  static final long CALCULATION_INTERVAL = 10L; // Seconds
  static final long SCAN_RATE = 2L; // Seconds

  private final Map<Section, AllocationRateCalculator> allocationCalculators;
  private final Map<Section, ActivityRateCalculator> activityCalculators;
  private final StatusSupplierCore<Activity> statusSupplierCore;
  private final EntityRegistry entityRegistry;

  public ActivitySupplierImpl(
    ActivationSupplier activationSupplier, EntityRegistry entityRegistry, EventBus eventBus, Logger logger )
  {
    super( null, new ActivationTracker( OBSERVATION_TIME, CALCULATION_INTERVAL ) );
    verifyNotNull( activationSupplier, "activationSupplier" );
    verifyNotNull( entityRegistry, "entityRegistry" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.allocationCalculators
      = createAllocationCalculators( entityRegistry, activationSupplier, OBSERVATION_TIME, CALCULATION_INTERVAL );
    this.activityCalculators = createActivityCalculators( entityRegistry, OBSERVATION_TIME, CALCULATION_INTERVAL );
    this.statusSupplierCore = new StatusSupplierCore<>( eventBus, calculateStatus(), this, logger );
    this.entityRegistry = entityRegistry;
  }

  @Override
  public Activity getStatus() {
    return statusSupplierCore.getStatus();
  }

  @Schedule( period = SCAN_RATE )
  void calculateRate() {
    statusSupplierCore.updateStatus( () -> calculateStatus(), INFO_ACTIVITY_RATE );
  }

  @Override
  @Schedule( period = CALCULATION_INTERVAL )
  void captureActivations() {
    super.captureActivations();
    allocationCalculators.values().forEach( calculator -> calculator.captureActivations() );
    activityCalculators.values().forEach( calculator -> calculator.captureActivations() );
  }

  @Override
  void setTimestampSupplier( Supplier<LocalDateTime> timestampSupplier ) {
    super.setTimestampSupplier( timestampSupplier );
    allocationCalculators.values().forEach( calculator -> calculator.setTimestampSupplier( timestampSupplier ) );
    activityCalculators.values().forEach( calculator -> calculator.setTimestampSupplier( timestampSupplier ) );
  }

  @Override
  protected boolean isActive() {
    return entityRegistry.findByDefinitionType( ActivationSensorDefinition.class )
      .stream()
      .map( sensor -> valueOf( sensor.isEngaged() ) )
      .reduce( ( engaged1, engaged2 ) -> valueOf( logicalOr( engaged1.booleanValue(), engaged2.booleanValue() ) ) )
      .get()
      .booleanValue();
  }

  private Activity calculateStatus() {
    Map<SectionDefinition, Percent> sectionAllocations = calculateSectionAllocationRate();
    Map<SectionDefinition, Percent> sectionActivities = calculateSectionActivityRate();
    return new Activity( super.calculate(), sectionActivities, sectionAllocations );
  }

  private Map<SectionDefinition, Percent> calculateSectionAllocationRate() {
    return allocationCalculators
        .keySet()
        .stream()
        .collect( toMap( section -> section.getDefinition(),
                         section -> allocationCalculators.get( section ).calculate() ) );
  }

  private Map<SectionDefinition, Percent> calculateSectionActivityRate() {
    return activityCalculators
      .keySet()
      .stream()
      .collect( toMap( section -> section.getDefinition(),
                       section -> activityCalculators.get( section ).calculate() ) );
  }
}