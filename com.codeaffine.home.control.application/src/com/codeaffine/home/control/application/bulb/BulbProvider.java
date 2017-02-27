package com.codeaffine.home.control.application.bulb;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.bulb.BulbProvider.Bulb;
import com.codeaffine.home.control.application.bulb.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.application.internal.bulb.BulbFactory;
import com.codeaffine.home.control.application.internal.bulb.BulbImpl;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.logger.LoggerFactory;

public class BulbProvider extends BaseEntityProvider<Bulb, BulbDefinition> {

  public static final int BULB_INTEGRITY_CHECK_INTERVAL = 2;

  public enum BulbDefinition implements EntityDefinition<Bulb> {
    KitchenCeiling, SinkUplight, ChimneyUplight, WindowUplight, DeskUplight, FanLight1, FanLight2, BedStand,
    BedRoomCeiling, BathRoomCeiling, HallCeiling;
  };

  public interface Bulb extends Entity<BulbDefinition> {
    void setOnOffStatus( OnOff onOffStatus );
    OnOff getOnOffStatus();
    void setBrightness( Percent brightness );
    Percent getBrightness();
    void setColorTemperature( Percent colorTemperature );
    Percent getColorTemperature();
  }

  public BulbProvider( Registry registry, LoggerFactory loggerFactory ) {
    super( new BulbFactory( registry, loggerFactory ) );
  }

  @Schedule( period = BULB_INTEGRITY_CHECK_INTERVAL )
  void ensureBulbStates() {
    forEachBulbDefintion( definition -> ( ( BulbImpl )findByDefinition( definition ) ).ensureStatusIntegrity() );
  }

  @Override
  protected Stream<BulbDefinition> getStreamOfDefinitions() {
    return Stream.of( BulbDefinition.values() );
  }

  private void forEachBulbDefintion( Consumer<? super BulbDefinition> action ) {
    getStreamOfDefinitions().forEach( action );
  }
}