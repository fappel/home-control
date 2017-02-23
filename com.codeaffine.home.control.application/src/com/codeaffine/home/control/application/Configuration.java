package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.BulbProvider.BulbDefinition.*;
import static com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition.*;
import static com.codeaffine.home.control.application.RoomProvider.RoomDefinition.*;
import static java.util.Arrays.asList;

import java.util.HashSet;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.application.internal.allocation.AdjacencyDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
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
    facility.equip( BedRoom ).with( BedStand, BedRoomCeiling, bedRoomMotion1 );
    facility.equip( Hall ).with( HallCeiling, hallMotion1 );
    facility.equip( Kitchen ).with( KitchenCeiling, SinkUplight, kitchenMotion1 );
    facility.equip( BathRoom ).with( BathRoomCeiling, bathRoomMotion1 );
    facility.equip( LivingRoom )
      .with( FanLight1, FanLight2, ChimneyUplight, DeskUplight, WindowUplight, livingRoomMotion1 );
  }

  @Override
  public void configureSystem( Context context ) {
    AdjacencyDefinition adjacencyDefinition
      = new AdjacencyDefinition( new HashSet<>( asList( BedRoom, Hall, Kitchen, BathRoom, LivingRoom ) ) );
    context.set( AdjacencyDefinition.class, adjacencyDefinition );
    adjacencyDefinition
      .link( BedRoom, LivingRoom )
      .link( LivingRoom, Kitchen )
      .link( LivingRoom, Hall )
      .link( Kitchen, Hall )
      .link( Kitchen, BathRoom );

    context.create( ZoneActivation.class );
    context.create( ActivityRate.class );
  }
}