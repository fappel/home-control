package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;

import java.util.Map;

import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider.NamedSceneConfiguration;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
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
      .whenStatusOf( GLOBAL, ActivationProvider.class ).matches( activation -> singleZoneReleaseOn( activation, HALL ) )
        .thenSelect( AwayScene.class )
      .otherwiseWhenStatusOf( ActivationProvider.class ).matches( activation -> singleZoneReleaseOn( activation, BED ) )
        .thenSelect( SleepScene.class )
      .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunZenitIsInTwilightZone( position ) )
        .thenSelect( TwilightScene.class )
      .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunIsAboveHorizon( position ) )
        .thenSelect( DayScene.class )
      .otherwiseSelect( NightScene.class );

    sceneSelector
      .whenStatusOf( HOTSPOT, NamedSceneProvider.class ).matches( selection -> selection.isActive() )
        .thenSelect( NamedSceneProvider.class, status -> status.getSceneType() )
      .otherwiseWhenStatusOf( ActivationProvider.class ).matches( activation -> workAreaIsSolelyActive( activation ) )
        .thenSelect( WorkAreaScene.class )
      .otherwiseSelect( NamedSceneProvider.class, status -> status.getSceneType() );
  }

  private static boolean singleZoneReleaseOn( Activation activation, EntityDefinition<?> zoneDefinition ) {
    return    activation.getAllZones().size() == 1
           && activation.getZone( zoneDefinition ).filter( zone -> zone.getReleaseTime().isPresent() ).isPresent();
  }

  private static boolean sunIsAboveHorizon( SunPosition position ) {
    return position.getZenit() > 0;
  }

  private static boolean sunZenitIsInTwilightZone( SunPosition position ) {
    return -18 <= position.getZenit() && position.getZenit() <= 0;
  }

  private static boolean workAreaIsSolelyActive( Activation activation ) {
    return activation.getAllZones().size() == 1 && activation.getZone( WORK_AREA ).isPresent();
  }
}