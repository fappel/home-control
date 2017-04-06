package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.HomeScope.*;
import static com.codeaffine.home.control.application.scene.HomeScope.KITCHEN;
import static com.codeaffine.home.control.application.scene.HomeScope.LIVING_ROOM;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubSection;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.supplier.NamedSceneSupplier;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier.NamedSceneConfiguration;
import com.codeaffine.home.control.status.supplier.SunPosition;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.status.test.util.supplier.NamedSceneSupplierTestProvider;
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

  private NamedSceneSupplierTestProvider namedSceneSupplierFactory;
  private SunPositionSupplier sunPositionSupplier;
  private ActivationSupplier activationSupplier;
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
    sunPositionSupplier = mock( SunPositionSupplier.class );
    activationSupplier = mock( ActivationSupplier.class );
    ActivitySupplier activitySupplier = mock( ActivitySupplier.class );
    context = new TestContext();
    namedSceneSupplierFactory = new NamedSceneSupplierTestProvider( context );
    context.set( SunPositionSupplier.class, sunPositionSupplier );
    context.set( Logger.class, mock( Logger.class ) );
    context.set( EventBus.class, mock( EventBus.class ) );
    context.set( ActivationSupplier.class, activationSupplier );
    context.set( ActivitySupplier.class, activitySupplier );
    context.set( NamedSceneConfiguration.class, new TestNamedSceneConfiguration() );
    context.set( NamedSceneSupplier.class, namedSceneSupplierFactory.get() );
    sceneSelector = new SceneSelectorImpl( context, mock( Logger.class ) );
    new SceneConfiguration().configureSceneSelection( sceneSelector );
  }

  @Test
  public void selectAtDayTime() {
    stubActivationSupplier( asStatus( zoneOf( WORK_AREA ), zoneOf( DINING_AREA ), zoneOf( BED ) ) );
    stubSunPositionSupplier( new SunPosition( 0.1, 4 ) );

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
    stubActivationSupplier( asStatus( zoneOf( WORK_AREA ), zoneOf( DINING_AREA ), zoneOf( BED ) ) );
    stubSunPositionSupplier( new SunPosition( -0.1, 4 ) );

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
    stubActivationSupplier( new Activation( emptySet() ) );
    stubSunPositionSupplier( new SunPosition( 0.1, 4 ) );

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
    stubActivationSupplier( asStatus( stubZone( stubSection( HALL ), now() ) ) );
    stubSunPositionSupplier( new SunPosition( 0.1, 4 ) );

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
    stubActivationSupplier( asStatus( zoneOf( WORK_AREA ), stubZone( stubSection( HALL ), now() ) ) );
    stubSunPositionSupplier( new SunPosition( 0.1, 4 ) );

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
    stubActivationSupplier( asStatus( zoneOf( WORK_AREA ), zoneOf( DINING_AREA ), zoneOf( BED ) ) );
    stubSunPositionSupplier( new SunPosition( 0.1, 4 ) );
    selectNamedScene( NAMED_SCENE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( HOME_SCOPE_VALUE_COUNT )
      .containsEntry( BED_ROOM, context.get( BedroomScene.class ) )
      .containsEntry( KITCHEN, context.get( KitchenScene.class ) )
      .containsEntry( GLOBAL, context.get( DayScene.class ) );
    assertThat( actual.get( LIVING_ROOM ).getName() ).isEqualTo( context.get( Scene1.class ).getName() );
  }

  private void stubSunPositionSupplier( SunPosition sunPosition ) {
    when( sunPositionSupplier.getStatus() ).thenReturn( sunPosition );
  }

  private static Zone zoneOf( SectionDefinition section ) {
    return stubZone( stubSection( section ) );
  }

  private void stubActivationSupplier( Activation activation ) {
    when( activationSupplier.getStatus() ).thenReturn( activation );
  }

  private void selectNamedScene( String sceneName ) {
    ChangeEvent<StringItem, StringType> event = stubChangeEvent( new StringType( sceneName ) );
    namedSceneSupplierFactory.onActiveSceneItemChange( event );
  }

  @SuppressWarnings( "unchecked" )
  private static ChangeEvent<StringItem, StringType> stubChangeEvent( StringType sceneSelectionName ) {
    ChangeEvent<StringItem, StringType> result = mock( ChangeEvent.class );
    when( result.getNewStatus() ).thenReturn( Optional.ofNullable( sceneSelectionName ) );
    return result;
  }
}