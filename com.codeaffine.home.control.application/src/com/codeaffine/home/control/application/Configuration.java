package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensorDefinition.*;
import static java.util.Arrays.asList;

import java.util.HashSet;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.application.internal.activity.ActivityProviderImpl;
import com.codeaffine.home.control.application.internal.scene.NamedSceneProviderImpl;
import com.codeaffine.home.control.application.internal.sun.SunPositionProviderImpl;
import com.codeaffine.home.control.application.internal.zone.ActivationProviderImpl;
import com.codeaffine.home.control.application.internal.zone.AdjacencyDefinition;
import com.codeaffine.home.control.application.lamp.LampProvider;
import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.operation.AdjustColorTemperatureOperation;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.scene.SceneConfiguration;
import com.codeaffine.home.control.application.section.SectionProvider;
import com.codeaffine.home.control.application.sensor.MotionSensorProvider;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider.NamedSceneConfiguration;
import com.codeaffine.home.control.application.status.SunPositionProvider;
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
    entityRegistry.register( SectionProvider.class );
  }

  @Override
  public void configureFacility( Facility facility ) {
    facility.equip( BEDROOM ).with( BED, DRESSING_AREA );
    facility.equip( BED ).with( BedStand, BED_MOTION );
    facility.equip( DRESSING_AREA ).with( BedRoomCeiling, DRESSING_AREA_MOTION );
    facility.equip( LIVING_ROOM ).with( LIVING_AREA, WORK_AREA );
    facility.equip( LIVING_AREA ).with( FanLight1, FanLight2, ChimneyUplight, WindowUplight, LIVING_AREA_MOTION );
    facility.equip( WORK_AREA ).with( DeskUplight, ChimneyUplight, WORK_AREA_MOTION );
    facility.equip( HALL ).with( HallCeiling, HALL_MOTION );
    facility.equip( KITCHEN ).with( COOKING_AREA, DINING_AREA );
    facility.equip( COOKING_AREA ).with( KitchenCeiling, COOKING_AREA_MOTION );
    facility.equip( DINING_AREA ).with( SinkUplight, DINING_AREA_MOTION );
    facility.equip( BATH_ROOM ).with( BathRoomCeiling, BATH_ROOM_MOTION );
  }

  @Override
  public void configureStatusProvider( StatusProviderRegistry statusProviderRegistry ) {
    AdjacencyDefinition adjacencyDefinition
      = new AdjacencyDefinition( new HashSet<>(
          asList( COOKING_AREA, DINING_AREA, BED, DRESSING_AREA, WORK_AREA, LIVING_AREA, HALL, BATH_ROOM ) ) );
    adjacencyDefinition
      .link( BED, DRESSING_AREA )
      .link( DRESSING_AREA, LIVING_AREA )
      .link( LIVING_AREA, WORK_AREA )
      .link( WORK_AREA, HALL )
      .link( HALL, COOKING_AREA )
      .link( COOKING_AREA, DINING_AREA )
      .link( DINING_AREA, BATH_ROOM );

    Context context = statusProviderRegistry.getContext();
    context.set( AdjacencyDefinition.class, adjacencyDefinition );
    statusProviderRegistry.register( ActivationProvider.class, ActivationProviderImpl.class );
    statusProviderRegistry.register( ActivityProvider.class, ActivityProviderImpl.class );
    statusProviderRegistry.register( SunPositionProvider.class, SunPositionProviderImpl.class );
    context.set( NamedSceneConfiguration.class, context.create( SceneConfiguration.class ) );
    statusProviderRegistry.register( NamedSceneProvider.class, NamedSceneProviderImpl.class );
  }

  @Override
  public void configureHomeControlOperations( ControlCenter controlCenter ) {
    controlCenter.registerOperation( LampSwitchOperation.class );
    controlCenter.registerOperation( AdjustBrightnessOperation.class );
    controlCenter.registerOperation( AdjustColorTemperatureOperation.class );
  }

  @Override
  public void configureSceneSelection( SceneSelector sceneSelector ) {
    new SceneConfiguration().configureSceneSelection( sceneSelector );
  }

  @Override
  public void configureSystem( Context context ) {
    context.set( ComputerIdleTime.class, context.create( ComputerIdleTime.class ) );
  }
}