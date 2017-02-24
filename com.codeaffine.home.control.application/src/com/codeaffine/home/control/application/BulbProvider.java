package com.codeaffine.home.control.application;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.application.internal.bulb.BulbFactory;
import com.codeaffine.home.control.application.internal.bulb.BulbImpl;
import com.codeaffine.home.control.entity.BaseEntityProvider;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.logger.LoggerFactory;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;

public class BulbProvider extends BaseEntityProvider<Bulb, BulbDefinition> {

  public enum BulbDefinition implements EntityDefinition<Bulb> {
    KitchenCeiling, SinkUplight, ChimneyUplight, WindowUplight, DeskUplight, FanLight1, FanLight2, BedStand,
    BedRoomCeiling, BathRoomCeiling, HallCeiling;
  };

  public interface Bulb extends Entity<BulbDefinition> {
    void setOnOffStatus( OnOffType onOffStatus );
    Optional<OnOffType> getOnOffStatus();
    void setBrightness( PercentType percent );
    Optional<PercentType> getBrightness();
    void setColorTemperature( PercentType percent );
    Optional<PercentType> getColorTemperature();
  }

  public BulbProvider( Registry registry, LoggerFactory loggerFactory ) {
    super( new BulbFactory( registry, loggerFactory ) );
  }

  @Schedule( period = 2 )
  void ensureBulbStates() {
    forEachBulbDefintion( definition -> ( ( BulbImpl )findByDefinition( definition ) ).ensure() );
  }

  @Override
  protected Stream<BulbDefinition> getStreamOfDefinitions() {
    return Stream.of( BulbDefinition.values() );
  }

  private void forEachBulbDefintion( Consumer<? super BulbDefinition> action ) {
    getStreamOfDefinitions().forEach( action );
  }
}