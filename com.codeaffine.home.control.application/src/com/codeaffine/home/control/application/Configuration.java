package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition.*;
import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.*;
import static java.util.Arrays.asList;

import java.util.HashSet;

import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.application.internal.activity.ActivityProviderImpl;
import com.codeaffine.home.control.application.internal.sun.SunPositionProviderImpl;
import com.codeaffine.home.control.application.internal.zone.AdjacencyDefinition;
import com.codeaffine.home.control.application.internal.zone.ZoneActivationProviderImpl;
import com.codeaffine.home.control.application.lamp.LampProvider;
import com.codeaffine.home.control.application.motion.MotionSensorProvider;
import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.operation.AdjustColorTemperatureOperation;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.room.RoomProvider;
import com.codeaffine.home.control.application.scene.HomeSceneSelectionConfigurer;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.status.ControlCenter;
import com.codeaffine.home.control.status.SceneSelector;
import com.codeaffine.home.control.status.StatusProviderRegistry;

public class Configuration implements SystemConfiguration {

  @Override
  public void configureEntities( EntityRegistry entityRegistry ) {
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
  public void configureStatusProvider( StatusProviderRegistry statusProviderRegistry ) {
    AdjacencyDefinition adjacencyDefinition
      = new AdjacencyDefinition( new HashSet<>( asList( BedRoom, Hall, Kitchen, BathRoom, LivingRoom ) ) );
    adjacencyDefinition
      .link( BedRoom, LivingRoom )
      .link( LivingRoom, Kitchen )
      .link( LivingRoom, Hall )
      .link( Kitchen, Hall )
      .link( Kitchen, BathRoom );

    statusProviderRegistry.getContext().set( AdjacencyDefinition.class, adjacencyDefinition );
    statusProviderRegistry.register( ZoneActivationProvider.class, ZoneActivationProviderImpl.class );
    statusProviderRegistry.register( ActivityProvider.class, ActivityProviderImpl.class );
    statusProviderRegistry.register( SunPositionProvider.class, SunPositionProviderImpl.class );
  }

  @Override
  public void configureHomeControlOperations( ControlCenter controlCenter ) {
    controlCenter.registerOperation( LampSwitchOperation.class );
    controlCenter.registerOperation( AdjustBrightnessOperation.class );
    controlCenter.registerOperation( AdjustColorTemperatureOperation.class );
  }

  @Override
  public void configureSceneSelection( SceneSelector sceneSelector ) {
    new HomeSceneSelectionConfigurer().configureSceneSelection( sceneSelector );
  }
}