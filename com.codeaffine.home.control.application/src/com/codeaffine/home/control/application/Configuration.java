package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition.*;
import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.*;
import static java.util.Arrays.asList;

import java.util.HashSet;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.application.control.ControlCenter;
import com.codeaffine.home.control.application.control.ControlCenter.SceneSelectionConfigurer;
import com.codeaffine.home.control.application.internal.activity.ActivityImpl;
import com.codeaffine.home.control.application.internal.control.ControlCenterImpl;
import com.codeaffine.home.control.application.internal.sun.SunPositionProviderImpl;
import com.codeaffine.home.control.application.internal.zone.AdjacencyDefinition;
import com.codeaffine.home.control.application.internal.zone.ZoneActivationImpl;
import com.codeaffine.home.control.application.lamp.LampProvider;
import com.codeaffine.home.control.application.motion.MotionSensorProvider;
import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.operation.AdjustColorTemperatureOperation;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.room.RoomProvider;
import com.codeaffine.home.control.application.sence.HomeSceneSelectionConfigurer;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;

public class Configuration implements SystemConfiguration {

  @Override
  public void registerEntities( EntityRegistry entityRegistry ) {
    entityRegistry.register( MotionSensorProvider.class );
    entityRegistry.register( LampProvider.class );
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

    context.set( SunPositionProvider.class, context.create( SunPositionProviderImpl.class ) );
    context.set( ZoneActivation.class, context.create( ZoneActivationImpl.class ) );
    context.set( Activity.class, context.create( ActivityImpl.class ) );
    context.set( SceneSelectionConfigurer.class, new HomeSceneSelectionConfigurer() );
    context.set( ControlCenter.class, context.create( ControlCenterImpl.class ) );
    context.get( ControlCenter.class ).registerOperation( LampSwitchOperation.class );
    context.get( ControlCenter.class ).registerOperation( AdjustBrightnessOperation.class );
    context.get( ControlCenter.class ).registerOperation( AdjustColorTemperatureOperation.class );
  }
}