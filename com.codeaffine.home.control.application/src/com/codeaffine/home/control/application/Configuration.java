package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition.*;
import static com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static java.util.Arrays.asList;

import java.util.HashSet;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.application.lamp.LampProvider;
import com.codeaffine.home.control.application.operation.AdjustBrightnessOperation;
import com.codeaffine.home.control.application.operation.AdjustColorTemperatureOperation;
import com.codeaffine.home.control.application.operation.LampCollector;
import com.codeaffine.home.control.application.operation.LampSwitchOperation;
import com.codeaffine.home.control.application.scene.LightThresholdUtil;
import com.codeaffine.home.control.application.scene.SceneConfiguration;
import com.codeaffine.home.control.application.util.LampControl;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.status.ControlCenter;
import com.codeaffine.home.control.status.SceneSelector;
import com.codeaffine.home.control.status.StatusConfiguration;
import com.codeaffine.home.control.status.StatusSupplierRegistry;
import com.codeaffine.home.control.status.supplier.AdjacencyDefinition;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier.NamedSceneConfiguration;

public class Configuration implements SystemConfiguration {

  private final StatusConfiguration statusConfiguration;
  private final SceneConfiguration sceneConfiguration;

  public Configuration() {
    statusConfiguration = new StatusConfiguration();
    sceneConfiguration = new SceneConfiguration();
  }

  @Override
  public void configureEntities( EntityRegistry entityRegistry ) {
    statusConfiguration.configureEntities( entityRegistry );
    entityRegistry.register( LampProvider.class );
  }

  @Override
  public void configureFacility( Facility facility ) {
    facility.equip( BEDROOM ).with( BED, BED_SIDE, DRESSING_AREA );
    facility.equip( BED ).with( BedStand, BedRoomCeiling, BED_MOTION, DRESSING_AREA_LUX );
    facility.equip( BED_SIDE ).with( BedStand, BedRoomCeiling, BED_SIDE_MOTION, DRESSING_AREA_LUX );
    facility.equip( DRESSING_AREA ).with( BedStand, BedRoomCeiling, DRESSING_AREA_MOTION, DRESSING_AREA_LUX );
    facility.equip( LIVING_ROOM ).with( LIVING_AREA, WORK_AREA );
    facility.equip( LIVING_AREA ).with( FanLight1, FanLight2, ChimneyUplight, WindowUplight, LIVING_AREA_MOTION, LIVING_AREA_LUX );
    facility.equip( WORK_AREA ).with( DeskUplight, ChimneyUplight, WORK_AREA_MOTION, WORK_AREA_LUX );
    facility.equip( HALL ).with( HallCeiling, HALL_MOTION, HALL_LUX );
    facility.equip( KITCHEN ).with( COOKING_AREA, DINING_AREA );
    facility.equip( COOKING_AREA ).with( KitchenCeiling, SinkUplight, COOKING_AREA_MOTION, DINING_AREA_LUX );
    facility.equip( DINING_AREA ).with( KitchenCeiling, SinkUplight, DINING_AREA_MOTION, DINING_AREA_LUX );
    facility.equip( BATH_ROOM ).with( BathRoomCeiling, BATH_ROOM_MOTION, BATH_ROOM_LUX );
  }

  @Override
  public void configureStatusSupplier( StatusSupplierRegistry statusSupplierRegistry ) {
    AdjacencyDefinition adjacencyDefinition
      = new AdjacencyDefinition( new HashSet<>(
          asList( COOKING_AREA, DINING_AREA, BED_SIDE, BED, DRESSING_AREA, WORK_AREA, LIVING_AREA, HALL, BATH_ROOM ) ) );
    adjacencyDefinition
      .link( BED_SIDE, BED )
      .link( BED, DRESSING_AREA )
      .link( DRESSING_AREA, LIVING_AREA )
      .link( LIVING_AREA, WORK_AREA )
      .link( WORK_AREA, HALL )
      .link( HALL, COOKING_AREA )
      .link( COOKING_AREA, DINING_AREA )
      .link( DINING_AREA, BATH_ROOM );

    Context context = statusSupplierRegistry.getContext();
    context.set( AdjacencyDefinition.class, adjacencyDefinition );
    context.set( NamedSceneConfiguration.class, sceneConfiguration );
    statusConfiguration.configureStatusSupplier( statusSupplierRegistry );
    context.set( LampCollector.class, context.create( LampCollector.class ) );
  }

  @Override
  public void configureHomeControlOperations( ControlCenter controlCenter ) {
    controlCenter.registerOperation( LampSwitchOperation.class );
    controlCenter.registerOperation( AdjustBrightnessOperation.class );
    controlCenter.registerOperation( AdjustColorTemperatureOperation.class );
    Context context = controlCenter.getContext();
    context.set( LampControl.class, context.create( LampControl.class ) );

    context.set( LightThresholdUtil.class, context.create( LightThresholdUtil.class ) );
  }

  @Override
  public void configureSceneSelection( SceneSelector sceneSelector ) {
    sceneConfiguration.configureSceneSelection( sceneSelector );
  }
}