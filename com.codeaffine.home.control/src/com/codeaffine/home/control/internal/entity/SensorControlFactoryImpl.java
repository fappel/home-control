package com.codeaffine.home.control.internal.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControl;
import com.codeaffine.home.control.entity.ZoneProvider.SensorControlFactory;

public class SensorControlFactoryImpl implements SensorControlFactory {

  private final ZoneProviderImpl zoneProvider;

  public SensorControlFactoryImpl( ZoneProviderImpl zoneProvider ) {
    this.zoneProvider = zoneProvider;
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>> SensorControl create( E zone ) {
    verifyNotNull( zone, "zone" );

    return new SensorControlImpl( zone, zoneProvider );
  }
}