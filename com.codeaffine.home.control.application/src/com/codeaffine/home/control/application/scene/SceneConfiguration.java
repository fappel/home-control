package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.*;
import static com.codeaffine.home.control.application.scene.HomeScope.KITCHEN;
import static com.codeaffine.home.control.application.scene.HomeScope.LIVING_ROOM;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;

import java.util.Map;
import java.util.stream.Stream;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier.NamedSceneConfiguration;
import com.codeaffine.home.control.status.supplier.SunPosition;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.status.EmptyScene;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector;

public class SceneConfiguration implements NamedSceneConfiguration {

  @Override
  public void configureNamedScenes( Map<String, Class<? extends Scene>> nameToSceneTypeMapping ) {
    nameToSceneTypeMapping.put( "ALL_OFF", AllLightsOffScene.class );
    nameToSceneTypeMapping.put( "HOME_CINEMA", HomeCinemaScene.class );
  }

  public void configureSceneSelection( SceneSelector sceneSelector ) {
    sceneSelector
      .whenStatusOf( GLOBAL, ActivationSupplier.class ).matches( activation -> singleZoneReleaseOn( activation, HALL ) )
        .thenSelect( AwayScene.class )
      .otherwiseWhenStatusOf( SunPositionSupplier.class ).matches( position -> sunIsAboveHorizon( position ) )
        .thenSelect( DayScene.class )
      .otherwiseSelect( NightScene.class );

    sceneSelector
      .whenStatusOf( LIVING_ROOM, NamedSceneSupplier.class ).matches( selection -> selection.isActive() )
        .thenSelect( NamedSceneSupplier.class, status -> status.getSceneType() )
      .otherwiseWhenStatusOf( ActivationSupplier.class )
        .matches( activation -> hasAnyZoneActivationsOf( activation, WORK_AREA, LIVING_AREA ) )
        .thenSelect( LivingRoomScene.class )
      .otherwiseSelect( NamedSceneSupplier.class, status -> status.getSceneType() );

    sceneSelector
      .whenStatusOf( KITCHEN, ActivationSupplier.class )
        .matches( activation -> hasAnyZoneActivationsOf( activation, COOKING_AREA, DINING_AREA ) )
        .thenSelect( KitchenScene.class )
      .otherwiseSelect( EmptyScene.class );

    sceneSelector
      .whenStatusOf( BED_ROOM, ActivationSupplier.class )
        .matches( activation -> hasAnyZoneActivationsOf( activation, BED, DRESSING_AREA ) )
        .thenSelect( BedroomScene.class )
      .otherwiseSelect( EmptyScene.class );
  }

  private static boolean singleZoneReleaseOn( Activation activation, EntityDefinition<?> zoneDefinition ) {
    return    activation.getAllZones().size() == 1
           && activation.getZone( zoneDefinition ).filter( zone -> zone.getReleaseTime().isPresent() ).isPresent();
  }

  private static boolean sunIsAboveHorizon( SunPosition position ) {
    return position.getZenit() > 0;
  }

  private static boolean hasAnyZoneActivationsOf( Activation activation, SectionDefinition ... zones ) {
    return Stream.of( zones ).anyMatch( zoneDefinition -> activation.isZoneActivated( zoneDefinition ) );
  }
}