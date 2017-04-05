package com.codeaffine.home.control.application.operation;

import static java.lang.Math.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.status.type.Percent;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusSupplier;

public class AdjustColorTemperatureOperation implements HomeControlOperation {

  private final EntityRegistry entityRegistry;

  public AdjustColorTemperatureOperation( EntityRegistry entityRegistry ) {
    this.entityRegistry = entityRegistry;
  }

  @Override
  public Collection<Class<? extends StatusSupplier<?>>> getRelatedStatusSupplierTypes() {
    return asList( SunPositionSupplier.class );
  }

  @Override
  public void reset() {
  }

  @Override
  public void executeOn( StatusEvent event ) {
    event.getSource( SunPositionSupplier.class ).ifPresent( sunPositionProvider -> {
      double zenitAngle = sunPositionProvider.getStatus().getZenit();
      double temperatureFactor = max( 2.0, ( abs( ( 10 + now().getDayOfYear() ) % 366 - 183 ) / 31 ) );
      double value = min( ( ( zenitAngle + 7 ) * temperatureFactor ), 100.0 );
      Percent colorTemperature = Percent.valueOf( ( int )( 100.0 - max( 0.0, value ) ) );
      Collection<Lamp> lamps = entityRegistry.findByDefinitionType( LampDefinition.class );
      lamps.forEach( lamp -> lamp.setColorTemperature( colorTemperature ) );
    } );
  }
}