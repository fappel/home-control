package com.codeaffine.home.control.application.operation;

import static java.lang.Math.*;
import static java.time.LocalDateTime.now;

import java.util.Collection;

import com.codeaffine.home.control.application.control.ControlCenterOperation;
import com.codeaffine.home.control.application.control.StatusEvent;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class AdjustColorTemperatureOperation implements ControlCenterOperation {

  private final EntityRegistry entityRegistry;

  public AdjustColorTemperatureOperation( EntityRegistry entityRegistry ) {
    this.entityRegistry = entityRegistry;
  }

  @Override
  public void prepare() {
  }

  @Override
  public void executeOn( StatusEvent event ) {
    event.getSource( SunPositionProvider.class ).ifPresent( sunPositionProvider -> {
      double zenitAngle = sunPositionProvider.getStatus().getZenit();
      double temperatureFactor = max( 2.0, ( abs( ( 10 + now().getDayOfYear() ) % 366 - 183 ) / 31 ) );
      double value = min( ( ( zenitAngle + 7 ) * temperatureFactor ), 100.0 );
      Percent colorTemperature = Percent.valueOf( ( int )( 100.0 - max( 0.0, value ) ) );
      Collection<Lamp> bulbs = entityRegistry.findByDefinitionType( LampDefinition.class );
      bulbs.forEach( bulb -> bulb.setColorTemperature( colorTemperature ) );
    } );
  }
}