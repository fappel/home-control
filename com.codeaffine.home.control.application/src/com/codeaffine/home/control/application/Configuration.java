package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.*;
import static com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition.*;
import static com.codeaffine.home.control.application.RoomProvider.RoomDefinition.*;


import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.entity.CompositeEntity;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.AllocationProvider.AllocationActor;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;

public class Configuration implements SystemConfiguration {

  @Override
  public void registerEntities( EntityRegistry entityRegistry ) {
    entityRegistry.register( MotionSensorProvider.class );
    entityRegistry.register( BulbProvider.class );
    entityRegistry.register( RoomProvider.class );
  }

  @Override
  public void configureFacility( Facility facility ) {
    facility.equip( BedRoom ).with( BedStand, BedRoomCeiling, bedRoomMotion );
    facility.equip( Hall ).with( HallCeiling, hallMotion );
    facility.equip( Kitchen ).with( KitchenCeiling, SinkUplight, kitchenMotion );
    facility.equip( BathRoom ).with( BathRoomCeiling, bathRoomMotion );
    facility.equip( LivingRoom )
      .with( FanLight1, FanLight2, ChimneyUplight, DeskUplight, WindowUplight, livingRoomMotion );
  }

  @Override
  public void configureSystem( Context context ) {
    context.get( EntityRegistry.class )
      .findAll()
      .stream()
      .filter( entity -> entity instanceof CompositeEntity )
      .map( entity -> ( CompositeEntity )entity )
      .forEach( composite -> {
        composite.getChildren()
          .stream()
          .filter( child -> child instanceof AllocationActor )
          .map( child -> ( AllocationActor )child )
          .forEach( actor -> actor.registerAllocatable( ( Entity<?> )composite ) );
      });

    context.create( Allocation.class );
    context.create( ActivityRate.class );
  }
}