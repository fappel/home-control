package com.codeaffine.home.control.application.lamp;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.internal.lamp.LampFactory;
import com.codeaffine.home.control.application.internal.lamp.LampImpl;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.status.type.OnOff;
import com.codeaffine.home.control.status.type.Percent;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.logger.LoggerFactory;

public class LampProvider extends BaseEntityProvider<Lamp, LampDefinition> {

  public static final int LAMP_INTEGRITY_CHECK_INTERVAL = 2;

  public enum LampDefinition implements EntityDefinition<Lamp> {
    KitchenCeiling, SinkUplight, ChimneyUplight, WindowUplight, DeskUplight, FanLight1, FanLight2, BedStand,
    BedRoomCeiling, BathRoomCeiling, HallCeiling;
  };

  public interface Lamp extends Entity<LampDefinition> {
    void setOnOffStatus( OnOff onOffStatus );
    OnOff getOnOffStatus();
    void setBrightness( Percent brightness );
    Percent getBrightness();
    void setColorTemperature( Percent colorTemperature );
    Percent getColorTemperature();
  }

  public LampProvider( Registry registry, LoggerFactory loggerFactory ) {
    super( new LampFactory( registry, loggerFactory ) );
  }

  @Schedule( period = LAMP_INTEGRITY_CHECK_INTERVAL )
  void ensureLampStates() {
    forEachLampDefinition( definition -> ( ( LampImpl )findByDefinition( definition ) ).ensureStatusIntegrity() );
  }

  @Override
  protected Stream<LampDefinition> getStreamOfDefinitions() {
    return Stream.of( LampDefinition.values() );
  }

  private void forEachLampDefinition( Consumer<? super LampDefinition> action ) {
    getStreamOfDefinitions().forEach( action );
  }
}