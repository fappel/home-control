package com.codeaffine.home.control.application.internal.scene;

import static com.codeaffine.home.control.application.internal.scene.Messages.INFO_NAMED_SCENE_SELECTION;
import static com.codeaffine.home.control.application.status.NamedSceneProvider.OFF;
import static com.codeaffine.home.control.application.test.EventBusHelper.captureEvent;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.NamedSceneProvider;
import com.codeaffine.home.control.application.status.NamedSceneProvider.NamedSceneConfiguration;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.EmptyScene;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.Scene1;
import com.codeaffine.home.control.type.StringType;

public class NamedSceneProviderImplTest {

  private static final StringType SCENE_SELECTION_NAME = new StringType( "SceneSelectionName" );
  private static final StringType UNKNOWN_SCENE_NAME = new StringType( "unknown" );

  private NamedSceneProviderImpl provider;
  private TestConfiguration configuration;
  private TestContext context;
  private EventBus eventBus;
  private Logger logger;

  static class TestConfiguration implements NamedSceneConfiguration {

    @Override
    public void configureNamedScenes( Map<String, Class<? extends Scene>> nameToSceneTypeMapping ) {
      nameToSceneTypeMapping.put( SCENE_SELECTION_NAME.toString(), Scene1.class );
    }
  }

  @Before
  public void setUp() {
    context = new TestContext();
    configuration = new TestConfiguration();
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    provider = new NamedSceneProviderImpl( context, configuration, eventBus, logger );
  }

  @Test
  public void onActiveSceneItemChange() {
    provider.onActiveSceneItemChange( stubChangeEvent( SCENE_SELECTION_NAME ) );
    Optional<NamedSceneProvider> actual = captureEvent( eventBus, NamedSceneProvider.class );

    assertThat( actual ).hasValue( provider );
    assertThat( provider.getStatus().getSceneType() ).isSameAs( Scene1.class );
    assertThat( provider.getStatus().isActive() ).isTrue();
    verify( logger ).info( INFO_NAMED_SCENE_SELECTION, provider.getStatus() );
  }

  @Test
  public void onActiveSceneItemChangeWithDefaultSelection() {
    provider.onActiveSceneItemChange( stubChangeEvent( SCENE_SELECTION_NAME ) );
    reset( eventBus );

    provider.onActiveSceneItemChange( stubChangeEvent( new StringType( NamedSceneProvider.OFF ) ) );
    Optional<NamedSceneProvider> actual = captureEvent( eventBus, NamedSceneProvider.class );

    assertThat( actual ).hasValue( provider );
    assertThat( provider.getStatus().getSceneType() ).isSameAs( EmptyScene.class );
    assertThat( provider.getStatus().isActive() ).isFalse();
    verify( logger ).info( INFO_NAMED_SCENE_SELECTION, provider.getStatus() );
  }

  @Test
  public void onActiveSceneItemChangeIfEventValueIsEmpty() {
    provider.onActiveSceneItemChange( stubChangeEvent( null ) );

    assertThat( provider.getStatus().getSceneType() ).isSameAs( EmptyScene.class );
    assertThat( provider.getStatus().isActive() ).isFalse();
    verify( eventBus, never() ).post( any() );
    verify( logger, never() ).info( INFO_NAMED_SCENE_SELECTION, provider.getStatus() );
  }

  @Test
  public void onActiveSceneItemChangeIfEventValueIsUnknown() {
    ChangeEvent<StringItem, StringType> event = stubChangeEvent( UNKNOWN_SCENE_NAME );

    Throwable actual = thrownBy( () -> provider.onActiveSceneItemChange( event ) );

    assertThat( actual )
      .hasMessageContaining( UNKNOWN_SCENE_NAME.toString() )
      .hasMessageContaining( SCENE_SELECTION_NAME.toString() )
      .hasMessageContaining( OFF )
      .isInstanceOf( IllegalArgumentException.class );
    assertThat( provider.getStatus().getSceneType() ).isSameAs( EmptyScene.class );
    assertThat( provider.getStatus().isActive() ).isFalse();
    verify( eventBus, never() ).post( any() );
    verify( logger, never() ).info( INFO_NAMED_SCENE_SELECTION, provider.getStatus() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsContextArgument() {
    new NamedSceneProviderImpl( null, configuration, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsConfigurationArgument() {
    new NamedSceneProviderImpl( context, null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new NamedSceneProviderImpl( context, configuration, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new NamedSceneProviderImpl( context, configuration, eventBus, null );
  }

  @SuppressWarnings( "unchecked" )
  private static ChangeEvent<StringItem, StringType> stubChangeEvent( StringType sceneSelectionName ) {
    ChangeEvent<StringItem, StringType> result = mock( ChangeEvent.class );
    when( result.getNewStatus() ).thenReturn( Optional.ofNullable( sceneSelectionName ) );
    return result;
  }
}