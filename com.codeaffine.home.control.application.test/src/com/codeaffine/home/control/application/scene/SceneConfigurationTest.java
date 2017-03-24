package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.GLOBAL;
import static com.codeaffine.home.control.application.scene.HomeScope.KITCHEN;
import static com.codeaffine.home.control.application.scene.HomeScope.LIVING_ROOM;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubSection;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.internal.scene.NamedSceneProviderImpl;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider.NamedSceneConfiguration;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.engine.status.SceneSelectorImpl;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.test.util.context.TestContext;

public class SceneConfigurationTest {

  private static final int HOME_SCOPE_VALUE_COUNT = HomeScope.values().length;

  private SunPositionProvider sunPositionProvider;
  private ActivationProvider activationProvider;
  private SceneSelectorImpl sceneSelector;
  private TestContext context;

  @Before
  public void setUp() {
    sunPositionProvider = mock( SunPositionProvider.class );
    activationProvider = mock( ActivationProvider.class );
    context = new TestContext();
    context.set( SunPositionProvider.class, sunPositionProvider );
    context.set( Logger.class, mock( Logger.class ) );
    context.set( EventBus.class, mock( EventBus.class ) );
    context.set( ActivationProvider.class, activationProvider );
    context.set( NamedSceneConfiguration.class, mock( NamedSceneConfiguration.class ) );
    context.set( NamedSceneProvider.class, context.create( NamedSceneProviderImpl.class ) );
    sceneSelector = new SceneSelectorImpl( context, mock( Logger.class ) );
    new SceneConfiguration().configureSceneSelection( sceneSelector );
  }

  @Test
  public void selectWithSunPositionHasZenitAboveHorizonAndArbitraryZoneActivation() {
    stubActivationProvider( asStatus( stubZone( stubSection( WORK_AREA ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
  }

  @Test
  public void selectWithSunPositionAfterSunsetOrBeforeSunriseAndArbitraryZoneActivation() {
    stubActivationProvider( asStatus( stubZone( stubSection( WORK_AREA ) ) ) );
    stubSunPositionProvider( new SunPosition( -0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( TwilightScene.class ) );
  }

  @Test
  public void selectWithSunPositionBeforeDawnOrAfterDuskAndArbitraryZoneActivation() {
    stubActivationProvider( asStatus( stubZone( stubSection( WORK_AREA ) ) ) );
    stubSunPositionProvider( new SunPosition( -18.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( NightScene.class ) );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsReleasedLeavingZone() {
    stubActivationProvider( asStatus( stubZone( stubSection( HALL ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( AwayScene.class ) );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsEngagedLeavingZone() {
    stubActivationProvider( asStatus( stubZone( stubSection( HALL ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
  }

  @Test
  public void selectOnArbitraySunPositionAndMultipleZoneActivationsIncludingReleasedLeavingZone() {
    Zone hallActivation = stubZone( stubSection( HALL ), now() );
    Zone livingRoomActivation = stubZone( stubSection( WORK_AREA ) );
    stubActivationProvider( asStatus( hallActivation, livingRoomActivation ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
  }

  @Test
  public void selectOnAribrarySunPositionAndSingleZoneActivationWhichIsReleasedBedZone() {
    stubActivationProvider( asStatus( stubZone( stubSection( BED ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( SleepScene.class ) );
  }

  @Test
  public void selectOnArbitrarySunPositionAndSingleZoneActivationWhichIsEngagedBedZone() {
    stubActivationProvider( asStatus( stubZone( stubSection( BED ) ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
  }

  @Test
  public void selectOnArbitraySunPositionAndMultipleZoneActivationsIncludingReleasedSleepingZone() {
    Zone hallActivation = stubZone( stubSection( BED ), now() );
    Zone livingRoomActivation = stubZone( stubSection( WORK_AREA ) );
    stubActivationProvider( asStatus( hallActivation, livingRoomActivation ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsKey( LIVING_ROOM )
      .containsKey( KITCHEN )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
  }

  private void stubSunPositionProvider( SunPosition sunPosition ) {
    when( sunPositionProvider.getStatus() ).thenReturn( sunPosition );
  }

  private void stubActivationProvider( Activation activation ) {
    when( activationProvider.getStatus() ).thenReturn( activation );
  }
}