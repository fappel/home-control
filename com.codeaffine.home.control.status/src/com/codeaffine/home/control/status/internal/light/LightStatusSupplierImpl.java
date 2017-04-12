package com.codeaffine.home.control.status.internal.light;

import static com.codeaffine.home.control.status.internal.light.Messages.INFO_MESSAGE_LIGHT_STATUS;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusSupplierCore;
import com.codeaffine.home.control.status.model.LightEvent;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.LightStatus;
import com.codeaffine.home.control.status.supplier.LightStatusSupplier;

public class LightStatusSupplierImpl implements LightStatusSupplier {

  private final Map<SectionDefinition, Integer> statusMap;
  private final StatusSupplierCore<LightStatus> core;

  public LightStatusSupplierImpl( EventBus eventBus, Logger logger ) {
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.core = new StatusSupplierCore<>( eventBus, new LightStatus( emptyMap() ), this, logger );
    this.statusMap = new HashMap<>();
  }

  @Override
  public LightStatus getStatus() {
    return core.getStatus();
  }

  @Subscribe
  void onLightStatusChange( LightEvent event ) {
    core.updateStatus( () -> apply( event ), INFO_MESSAGE_LIGHT_STATUS );
  }

  private LightStatus apply( LightEvent event ) {
    statusMap.putAll( collectSectionToLightValueMappings( event ) );
    return new LightStatus( statusMap );
  }

  private static Map<SectionDefinition, Integer> collectSectionToLightValueMappings( LightEvent event ) {
    return event
      .getAffected()
      .stream()
      .collect( toMap( entity -> ( SectionDefinition )entity.getDefinition(), entity -> event.getSensorStatus() ) );
  }
}