package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.*;
import static com.codeaffine.home.control.application.RoomProvider.RoomDefinition.*;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.item.ContactItem;

public class Configuration implements SystemConfiguration {

  @Override
  public void registerEntities( EntityRegistry entityRegistry ) {
    entityRegistry.register( BulbProvider.class );
    entityRegistry.register( RoomProvider.class );
  }

  @Override
  public void configureFacility( Facility facility ) {
    facility.equip( BedRoom ).with( BedStand, BedRoomCeiling );
    facility.equip( LivingRoom ).with( FanLight1, FanLight2, ChimneyUplight, DeskUplight, WindowUplight );
    facility.equip( Hall ).with( HallCeiling );
    facility.equip( Kitchen ).with( KitchenCeiling, SinkUplight );
    facility.equip( BathRoom ).with( BathRoomCeiling );
  }

  @Override
  public void configureSystem( Context context ) {
    Collection<Bulb> livingRoomBulbs = context.get( EntityRegistry.class )
                                              .findByDefinition( LivingRoom )
                                              .getChildren( BulbDefinition.class );
    LoggerFactory.getLogger( Configuration.class ).info( "living room bulbs: " + livingRoomBulbs );

    Registry registry = context.get( Registry.class );
    Stream.of( RoomOld.values() ).forEach( room -> {
      room.registerSensorItems( registry.getItem( room.getVariablePrefix() + "Motion", ContactItem.class ) );
    } );
    context.create( ActivityRate.class );
  }
}