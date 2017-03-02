package com.codeaffine.home.control.application.sence;

import com.codeaffine.home.control.application.control.ControlCenter.SceneSelectionConfigurer;

import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.BedRoom;
import static com.codeaffine.home.control.application.type.Percent.P_020;

import com.codeaffine.home.control.application.Activity;
import com.codeaffine.home.control.application.SunPositionProvider;
import com.codeaffine.home.control.application.ZoneActivation;
import com.codeaffine.home.control.application.control.SceneSelector;

public class HomeSceneSelectionConfigurer implements SceneSelectionConfigurer {

  @Override
  public void configure( SceneSelector sceneSelector ) {
    sceneSelector.whenStatusOf( SunPositionProvider.class ).matches( position -> position.getZenit() > 10 )
      .whenStatusOf( Activity.class ).matches( rate -> rate.compareTo( P_020 ) > 0 )
        .thenSelect( SleepTimeScene.class )
      .otherwiseWhenStatusOf( ZoneActivation.class ).matches( zones -> zones.contains( BedRoom ) )
        .or( Activity.class ).matches( rate -> true )
        .thenSelect( SleepTimeScene.class )
    .otherwiseSelect( SleepTimeScene.class );
  }
}
