package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;

import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider.NamedSceneConfiguration;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector;

public class SceneConfiguration implements NamedSceneConfiguration {

  @Override
  public void configureNamedScenes( Map<String, Class<? extends Scene>> sceneType ) {

  }

  public void configureSceneSelection( SceneSelector sceneSelector ) {
    sceneSelector
      .whenStatusOf( GLOBAL, ZoneActivationProvider.class ).matches( zones -> singleZoneReleaseOn( zones, HALL ) )
        .thenSelect( AwayScene.class )
      .otherwiseWhenStatusOf( ZoneActivationProvider.class ).matches( zones -> singleZoneReleaseOn( zones, BED ) )
        .thenSelect( SleepScene.class )
      .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunZenitIsInTwilightZone( position ) )
        .thenSelect( TwilightScene.class )
      .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunIsAboveHorizon( position ) )
        .thenSelect( DayScene.class )
      .otherwiseSelect( NightScene.class );

    sceneSelector
      .whenStatusOf( HOTSPOT, NamedSceneProvider.class ).matches( selection -> selection.isActive() )
        .thenSelect( NamedSceneProvider.class, status -> status.getSceneType() )
      .otherwiseSelect( NamedSceneProvider.class, status -> status.getSceneType() );
  }

  private static boolean singleZoneReleaseOn( Set<ZoneActivation> zones, EntityDefinition<?> zoneDefinition ) {
    return zones.size() == 1
        && zones.iterator().next().getZone().getDefinition() == zoneDefinition
        && zones.iterator().next().getReleaseTime().isPresent();
  }

  private static boolean sunIsAboveHorizon( SunPosition position ) {
    return position.getZenit() > 0;
  }

  private static boolean sunZenitIsInTwilightZone( SunPosition position ) {
    return -18 <= position.getZenit() && position.getZenit() <= 0;
  }
}