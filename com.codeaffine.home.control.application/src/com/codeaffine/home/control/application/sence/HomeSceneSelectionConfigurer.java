package com.codeaffine.home.control.application.sence;

import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.Hall;
import static java.time.LocalDateTime.now;

import java.util.Set;

import com.codeaffine.home.control.application.control.ControlCenter.SceneSelectionConfigurer;
import com.codeaffine.home.control.application.control.SceneSelector;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;

public class HomeSceneSelectionConfigurer implements SceneSelectionConfigurer {

  @Override
  public void configure( SceneSelector sceneSelector ) {
    sceneSelector.whenStatusOf( ZoneActivationProvider.class ).matches( zones -> lastPersonHasLeftHall( zones ) )
      .thenSelect( AwayScene.class )
    .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunIsAboveHorizon( position ) )
      .thenSelect( DayScene.class )
    .otherwiseWhenStatusOf( SunPositionProvider.class ).matches( position -> sunZenitIsInTwilightZone( position ) )
      .thenSelect( TwilightScene.class )
    .otherwiseSelect( NightScene.class );
  }

  private static boolean lastPersonHasLeftHall( Set<ZoneActivation> zones ) {
    return zones.size() == 1
        && zones.iterator().next().getZone().getDefinition() == Hall
        && zones.iterator().next().getReleaseTime().isPresent();
//        && zones.iterator().next().getReleaseTime().get().plusSeconds( 40L ).isBefore( now() );
  }

  private static boolean sunIsAboveHorizon( SunPosition position ) {
    return position.getZenit() > 0;
  }

  private static boolean sunZenitIsInTwilightZone( SunPosition position ) {
    return -18 <= position.getZenit() && position.getZenit() <= 0;
  }
}