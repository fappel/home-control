package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.type.OnOffType.ON;
import static java.lang.Math.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusSupplier;
import com.codeaffine.home.control.status.supplier.HeartBeatSupplier;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.status.type.Percent;

public class AdjustColorTemperatureOperation implements HomeControlOperation {

  private static final int COLOR_TEMPERATURE_DEFAULT = 50;

  private final SwitchItem autoColorTemperatureSwitch;
  private final NumberItem colorTemperatureItem;
  private final EntityRegistry entityRegistry;

  private Percent colorTemperature;

  public AdjustColorTemperatureOperation( EntityRegistry entityRegistry,
                                          @ByName( "autoColorTemperature" ) SwitchItem autoColorTemperatureSwitch,
                                          @ByName( "colorTemperature" ) NumberItem colorTemperatureItem ) {
    this.autoColorTemperatureSwitch = autoColorTemperatureSwitch;
    this.colorTemperatureItem = colorTemperatureItem;
    this.entityRegistry = entityRegistry;
    this.colorTemperature = getColorTemperatureSetting();
  }

  @Override
  public Collection<Class<? extends StatusSupplier<?>>> getRelatedStatusSupplierTypes() {
    return asList( SunPositionSupplier.class, HeartBeatSupplier.class );
  }

  @Override
  public void reset() {
  }

  @Override
  public void executeOn( StatusEvent event ) {
    event.getSource( SunPositionSupplier.class ).ifPresent( sunPositionSupplier -> {
      if( isAutoColorTemperatureOn() ) {
        colorTemperature = calculateColorTemperature( sunPositionSupplier );
        adjustColorTemperatureOfLamps( colorTemperature );
      } else {
        adjustColorTemperatureOfLamps( getColorTemperatureSetting() );
      }
    } );

    event.getSource( HeartBeatSupplier.class ).ifPresent( heartBeat -> {
      adjustColorTemperatureOfLamps( isAutoColorTemperatureOn() ? colorTemperature : getColorTemperatureSetting() );
    });
  }

  private boolean isAutoColorTemperatureOn() {
    return autoColorTemperatureSwitch.getStatus( ON ) == ON;
  }

  private static Percent calculateColorTemperature( SunPositionSupplier sunPositionSupplier ) {
    double zenitAngle = sunPositionSupplier.getStatus().getZenit();
    double temperatureFactor = max( 2.0, ( abs( ( 10 + now().getDayOfYear() ) % 366 - 183 ) / 31 ) );
    double value = min( ( ( zenitAngle + 7 ) * temperatureFactor ), 100.0 );
    return Percent.valueOf( ( int )( 100.0 - max( 0.0, value ) ) );
  }

  private void adjustColorTemperatureOfLamps( Percent colorTemperature ) {
    Collection<Lamp> lamps = entityRegistry.findByDefinitionType( LampDefinition.class );
    lamps.forEach( lamp -> lamp.setColorTemperature( colorTemperature ) );
  }

  private Percent getColorTemperatureSetting() {
    return Percent.valueOf( colorTemperatureItem.getStatus( COLOR_TEMPERATURE_DEFAULT ) );
  }
}