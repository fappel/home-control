package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.GLOBAL;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubZone;
import static com.codeaffine.home.control.application.test.ZoneActivationHelper.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.engine.status.SceneSelectorImpl;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.test.util.context.TestContext;

public class HomeSceneSelectionConfigurerTest {

  private ZoneActivationProvider zoneActivationProvider;
  private SunPositionProvider sunPositionProvider;
  private SceneSelectorImpl sceneSelector;
  private TestContext context;

  @Before
  public void setUp() {
    sunPositionProvider = mock( SunPositionProvider.class );
    zoneActivationProvider = mock( ZoneActivationProvider.class );
    context = new TestContext();
    context.set( SunPositionProvider.class, sunPositionProvider );
    context.set( ZoneActivationProvider.class, zoneActivationProvider );
    sceneSelector = new SceneSelectorImpl( context, mock( Logger.class ) );
    new HomeSceneSelectionConfigurer().configureSceneSelection( sceneSelector );
  }

  @Test
  public void selectWithSunPositionHasZenitAboveHorizonAndArbitraryZoneActivation() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( WORK_AREA ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( DayScene.class ) );
  }

  @Test
  public void selectWithSunPositionAfterSunsetOrBeforeSunriseAndArbitraryZoneActivation() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( WORK_AREA ) ) ) );
    stubSunPositionProvider( new SunPosition( -0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( TwilightScene.class ) );
  }

  @Test
  public void selectWithSunPositionBeforeDawnOrAfterDuskAndArbitraryZoneActivation() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( WORK_AREA ) ) ) );
    stubSunPositionProvider( new SunPosition( -18.1, 4 ) );


    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( NightScene.class ) );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsReleasedLeavingZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( HALL ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( AwayScene.class ) );

  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsEngagedLeavingZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( HALL ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( DayScene.class ) );
  }

  @Test
  public void selectOnArbitraySunPositionAndMultipleZoneActivationsIncludingReleasedLeavingZone() {
    ZoneActivation hallActivation = stubZoneActivation( stubZone( HALL ), now() );
    ZoneActivation livingRoomActivation = stubZoneActivation( stubZone( WORK_AREA ) );
    stubZoneActivationProvider( asStatus( hallActivation, livingRoomActivation ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( DayScene.class ) );
  }

  @Test
  public void selectOnAribrarySunPositionAndSingleZoneActivationWhichIsReleasedBedZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( BED ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( SleepScene.class ) );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsEngagedBedZone() {
    stubZoneActivationProvider( asStatus( stubZoneActivation( stubZone( BED ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( DayScene.class ) );
  }

  @Test
  public void selectOnArbitraySunPositionAndMultipleZoneActivationsIncludingReleasedSleepingZone() {
    ZoneActivation hallActivation = stubZoneActivation( stubZone( BED ), now() );
    ZoneActivation livingRoomActivation = stubZoneActivation( stubZone( WORK_AREA ) );
    stubZoneActivationProvider( asStatus( hallActivation, livingRoomActivation ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );


    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( DayScene.class ) );
  }

  private void stubSunPositionProvider( SunPosition sunPosition ) {
    when( sunPositionProvider.getStatus() ).thenReturn( sunPosition );
  }

  private void stubZoneActivationProvider( Set<ZoneActivation> zoneActivations ) {
    when( zoneActivationProvider.getStatus() ).thenReturn( zoneActivations );
  }
}