package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.*;
import static com.codeaffine.home.control.application.scene.HomeScope.KITCHEN;
import static com.codeaffine.home.control.application.scene.HomeScope.LIVING_ROOM;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubSection;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.internal.scene.NamedSceneProviderImpl;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider.NamedSceneConfiguration;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.engine.status.SceneSelectorImpl;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.EmptyScene;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.Scene1;
import com.codeaffine.home.control.type.StringType;

public class SceneConfigurationTest {

  private static final int HOME_SCOPE_VALUE_COUNT = HomeScope.values().length;
  private static final String NAMED_SCENE = "namedScene";

  private SunPositionProvider sunPositionProvider;
  private ActivationProvider activationProvider;
  private SceneSelectorImpl sceneSelector;
  private TestContext context;

  static class TestNamedSceneConfiguration implements NamedSceneConfiguration {

    @Override
    public void configureNamedScenes( Map<String, Class<? extends Scene>> nameToSceneTypeMapping ) {
      nameToSceneTypeMapping.put( NAMED_SCENE, Scene1.class );
    }
  }

  @Before
  public void setUp() {
    sunPositionProvider = mock( SunPositionProvider.class );
    activationProvider = mock( ActivationProvider.class );
    ActivityProvider activityProvider = mock( ActivityProvider.class );
    context = new TestContext();
    context.set( SunPositionProvider.class, sunPositionProvider );
    context.set( Logger.class, mock( Logger.class ) );
    context.set( EventBus.class, mock( EventBus.class ) );
    context.set( ActivationProvider.class, activationProvider );
    context.set( ActivityProvider.class, activityProvider );
    context.set( NamedSceneConfiguration.class, new TestNamedSceneConfiguration() );
    context.set( NamedSceneProvider.class, context.create( NamedSceneProviderImpl.class ) );
    sceneSelector = new SceneSelectorImpl( context, mock( Logger.class ) );
    new SceneConfiguration().configureSceneSelection( sceneSelector );
  }

  @Test
  public void selectAtDayTime() {
    stubActivationProvider( asStatus( zoneOf( WORK_AREA ), zoneOf( DINING_AREA ), zoneOf( BED ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsEntry( LIVING_ROOM, context.get( LivingRoomScene.class ) )
      .containsEntry( BED_ROOM, context.get( BedroomScene.class ) )
      .containsEntry( KITCHEN, context.get( KitchenScene.class ) )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
  }

  @Test
  public void selectAtNightTime() {
    stubActivationProvider( asStatus( zoneOf( WORK_AREA ), zoneOf( DINING_AREA ), zoneOf( BED ) ) );
    stubSunPositionProvider( new SunPosition( -0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsEntry( LIVING_ROOM, context.get( LivingRoomScene.class ) )
      .containsEntry( BED_ROOM, context.get( BedroomScene.class ) )
      .containsEntry( KITCHEN, context.get( KitchenScene.class ) )
      .containsEntry( GLOBAL, context.get( NightScene.class ) );
  }

  @Test
  public void selectWithoutActivation() {
    stubActivationProvider( new Activation( emptySet() ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsEntry( BED_ROOM, context.get( EmptyScene.class ) )
      .containsEntry( KITCHEN, context.get( EmptyScene.class ) )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
    assertThat( actual.get( LIVING_ROOM ).getName() ).isEqualTo( context.get( EmptyScene.class ).getName() );
  }

  @Test
  public void selectWithSingleZoneReleaseOnHall() {
    stubActivationProvider( asStatus( stubZone( stubSection( HALL ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsEntry( BED_ROOM, context.get( EmptyScene.class ) )
      .containsEntry( KITCHEN, context.get( EmptyScene.class ) )
      .containsEntry( GLOBAL, context.get( AwayScene.class ) );
    assertThat( actual.get( LIVING_ROOM ).getName() ).isEqualTo( context.get( EmptyScene.class ).getName() );
  }

  @Test
  public void selectWithZoneReleaseOnHallButAdditionalActivation() {
    stubActivationProvider( asStatus( zoneOf( WORK_AREA ), stubZone( stubSection( HALL ), now() ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsEntry( LIVING_ROOM, context.get( LivingRoomScene.class ) )
      .containsEntry( BED_ROOM, context.get( EmptyScene.class ) )
      .containsEntry( KITCHEN, context.get( EmptyScene.class ) )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
  }

  @Test
  public void selectWithNamedSceneSelection() {
    stubActivationProvider( asStatus( zoneOf( WORK_AREA ), zoneOf( DINING_AREA ), zoneOf( BED ) ) );
    stubSunPositionProvider( new SunPosition( 0.1, 4 ) );
    selectNamedScene( NAMED_SCENE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsEntry( BED_ROOM, context.get( BedroomScene.class ) )
      .containsEntry( KITCHEN, context.get( KitchenScene.class ) )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
    assertThat( actual.get( LIVING_ROOM ).getName() ).isEqualTo( context.get( Scene1.class ).getName() );
  }

  private void stubSunPositionProvider( SunPosition sunPosition ) {
    when( sunPositionProvider.getStatus() ).thenReturn( sunPosition );
  }

  private static Zone zoneOf( SectionDefinition section ) {
    return stubZone( stubSection( section ) );
  }

  private void stubActivationProvider( Activation activation ) {
    when( activationProvider.getStatus() ).thenReturn( activation );
  }

  private void selectNamedScene( String sceneName ) {
    ChangeEvent<StringItem, StringType> event = stubChangeEvent( new StringType( sceneName ) );
    (( NamedSceneProviderImpl )context.get( NamedSceneProvider.class ) ).onActiveSceneItemChange( event );
  }

  @SuppressWarnings( "unchecked" )
  private static ChangeEvent<StringItem, StringType> stubChangeEvent( StringType sceneSelectionName ) {
    ChangeEvent<StringItem, StringType> result = mock( ChangeEvent.class );
    when( result.getNewStatus() ).thenReturn( Optional.ofNullable( sceneSelectionName ) );
    return result;
  }
}