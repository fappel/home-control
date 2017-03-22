package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.Messages.INFO_ACTIVITY_RATE;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.Boolean.*;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusProviderCore;

public class ActivityProviderImpl implements ActivityProvider {

  static final long OBSERVATION_TIME_FRAME = 3L; // Minutes
  static final long CALCULATION_INTERVAL = 10L; // Seconds
  static final long SCAN_RATE = 2L; // Seconds

  private final Map<Section, SectionActivityProvider> sectionProviders;
  private final StatusProviderCore<Activity> statusProviderCore;
  private final ActivationTracker activationTracker;
  private final EntityRegistry entityRegistry;

  public ActivityProviderImpl( EntityRegistry entityRegistry, EventBus eventBus, Logger logger ) {
    verifyNotNull( entityRegistry, "entityRegistry" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.sectionProviders = createSectionProviders( entityRegistry, OBSERVATION_TIME_FRAME, CALCULATION_INTERVAL );
    this.activationTracker = newActivationTracker( OBSERVATION_TIME_FRAME, CALCULATION_INTERVAL );
    this.statusProviderCore = new StatusProviderCore<>( eventBus, calculateStatus(), this, logger );
    this.entityRegistry = entityRegistry;
  }

  @Override
  public Activity getStatus() {
    return statusProviderCore.getStatus();
  }

  @Schedule( period = SCAN_RATE )
  void calculateRate() {
    statusProviderCore.updateStatus( () -> calculateStatus(), INFO_ACTIVITY_RATE );
  }

  @Schedule( period = CALCULATION_INTERVAL )
  void captureActivations() {
    if( hasActiveSensors() ) {
      activationTracker.captureActivation();
    }
    activationTracker.removeExpired();
    sectionProviders.values().forEach( provider -> provider.captureSensorActivations() );
  }

  void setTimestampSupplier( Supplier<LocalDateTime> timestampSupplier ) {
    activationTracker.setTimestampSupplier( timestampSupplier );
    sectionProviders.values().forEach( provider -> provider.setTimestampSupplier( timestampSupplier ) );
  }

  private boolean hasActiveSensors() {
    return entityRegistry.findByDefinitionType( ActivationSensorDefinition.class )
      .stream()
      .map( sensor -> valueOf( sensor.isEngaged() ) )
      .reduce( ( engaged1, engaged2 ) -> valueOf( logicalOr( engaged1.booleanValue(), engaged2.booleanValue() ) ) )
      .get()
      .booleanValue();
  }

  private Activity calculateStatus() {
    Percent overallRate = activationTracker.calculateRate();
    Map<SectionDefinition, Percent> sectionActivities = calculateSectionActivityRate();
    return new Activity( overallRate, sectionActivities );
  }

  private Map<SectionDefinition, Percent> calculateSectionActivityRate() {
    return sectionProviders
      .keySet()
      .stream()
      .collect( toMap( section -> section.getDefinition(),
                       section -> sectionProviders.get( section ).calculateRate() ) );
  }

  private static Map<Section, SectionActivityProvider> createSectionProviders(
    EntityRegistry entityRegistry, long timeFrame, long intervalDuration )
  {
    return entityRegistry
      .findByDefinitionType( SectionDefinition.class )
      .stream()
      .filter( section -> !section.getChildren( ActivationSensorDefinition.class ).isEmpty() )
      .collect( toMap( section -> section,
                       section -> newSectionActivityProvider( section, timeFrame, intervalDuration ) ) );
  }

  private static SectionActivityProvider newSectionActivityProvider(
    Section section, long timeFrame, long intervalDuration )
  {
    return new SectionActivityProvider( section, newActivationTracker( timeFrame, intervalDuration ) );
  }

  private static ActivationTracker newActivationTracker( long timeFrame, long intervalDuration ) {
    return new ActivationTracker( timeFrame, intervalDuration );
  }
}