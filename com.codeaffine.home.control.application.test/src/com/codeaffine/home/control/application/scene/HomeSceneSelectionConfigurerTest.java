package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.room.RoomProvider.RoomDefinition.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubZone;
import static com.codeaffine.home.control.application.test.ZoneActivationHelper.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.scene.AwayScene;
import com.codeaffine.home.control.application.scene.DayScene;
import com.codeaffine.home.control.application.scene.HomeSceneSelectionConfigurer;
import com.codeaffine.home.control.application.scene.NightScene;
import com.codeaffine.home.control.application.scene.SleepScene;
import com.codeaffine.home.control.application.scene.TwilightScene;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.engine.status.SceneSelectorImpl;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.test.util.context.TestContext;

public class HomeSceneSelectionConfigurerTest {

  private ZoneActivationProvider zoneActivationProvider;
  private SunPositionProvider sunPositionProvider;
  private SceneSelectorImpl sceneSelector;

  @Before
  public void setUp() {
    sunPositionProvider = mock( SunPositionProvider.class );
    zoneActivationProvider = mock( ZoneActivationProvider.class );
    TestContext context = new TestContext();
    context.set( SunPositionProvider.class, sunPositionProvider );
    context.set( ZoneActivationProvider.class, zoneActivationProvider );
    sceneSelector = new SceneSelectorImpl( context, mock( Logger.class ) );
    new HomeSceneSelectionConfigurer().configureSceneSelection( sceneSelector );
  }

  @Test
  public void selectWithSunPositionHasZenitAboveHorizonAndArbitraryZoneActivation() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( LivingRoom ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( DayScene.class );
  }

  @Test
  public void selectWithSunPositionAfterSunsetOrBeforeSunriseAndArbitraryZoneActivation() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( LivingRoom ) ) ) );
    stubSunPositionProvider( new SunPosition( -0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( TwilightScene.class );
  }

  @Test
  public void selectWithSunPositionBeforeDawnOrAfterDuskAndArbitraryZoneActivation() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( LivingRoom ) ) ) );
    stubSunPositionProvider( new SunPosition( -18.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( NightScene.class );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsReleasedLeavingZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( Hall ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( AwayScene.class );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsEngagedLeavingZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( Hall ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( DayScene.class );
  }

  @Test
  public void selectOnArbitraySunPositionAndMultipleZoneActivationsIncludingReleasedLeavingZone() {
    ZoneActivation hallActivation = stubZoneActivation( stubZone( Hall ), now() );
    ZoneActivation livingRoomActivation = stubZoneActivation( stubZone( LivingRoom ) );
    stubZoneActivationProvider( asStatus( hallActivation, livingRoomActivation ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( DayScene.class );
  }

  @Test
  public void selectOnAribrarySunPositionAndSingleZoneActivationWhichIsReleasedBedRoomZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( BedRoom ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( SleepScene.class );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsEngagedBedRoomZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( BedRoom ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( DayScene.class );
  }

  @Test
  public void selectOnArbitraySunPositionAndMultipleZoneActivationsIncludingReleasedSleepingZone() {
    ZoneActivation hallActivation = stubZoneActivation( stubZone( BedRoom ), now() );
    ZoneActivation livingRoomActivation = stubZoneActivation( stubZone( LivingRoom ) );
    stubZoneActivationProvider( asStatus( hallActivation, livingRoomActivation ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( DayScene.class );
  }

  private void stubSunPositionProvider( SunPosition sunPosition ) {
    when( sunPositionProvider.getStatus() ).thenReturn( sunPosition );
  }

  private void stubZoneActivationProvider( Set<ZoneActivation> zoneActivations ) {
    when( zoneActivationProvider.getStatus() ).thenReturn( zoneActivations );
  }
}