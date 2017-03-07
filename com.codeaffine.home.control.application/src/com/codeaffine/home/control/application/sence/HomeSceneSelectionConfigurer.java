package com.codeaffine.home.control.application.sence;

import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.*;

import java.util.Set;

import com.codeaffine.home.control.application.control.ControlCenter.SceneSelectionConfigurer;
import com.codeaffine.home.control.application.control.SceneSelector;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class HomeSceneSelectionConfigurer implements SceneSelectionConfigurer {

  @Override
  public void configure( SceneSelector sceneSelector ) {
    sceneSelector.whenStatusOf( ZoneActivationProvider.class ).matches( zones -> singleZoneReleaseOn( zones, Hall ) )
      .thenSelect( AwayScene.class )
    .otherwiseWhenStatusOf( ZoneActivationProvider.class ).matches( zones -> singleZoneReleaseOn( zones, BedRoom ) )
      .thenSelect( SleepScene.class )
    .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunZenitIsInTwilightZone( position ) )
      .thenSelect( TwilightScene.class )
    .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunIsAboveHorizon( position ) )
      .thenSelect( DayScene.class )
    .otherwiseSelect( NightScene.class );
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