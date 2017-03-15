package com.codeaffine.home.control.engine.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatus;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;

public class DynamicSceneProxyTest {

  private static final String ACTIVATE_SCENE1 = "activate Scene1";
  private static final String DEACTIVATE_SCENE1 = "deactivate Scene1";
  private static final String ACTIVATE_SCENE2 = "activate Scene2";
  private static final String DEACTIVATE_SCENE2 = "deactivate Scene2";

  private MyStatusProvider statusProvider;
  private ArrayList<Object> log;
  private TestContext context;
  private Scene proxy;

  static class Scene1 implements Scene {

    private final List<Object> log;

    Scene1( List<Object> log ) {
      this.log = log;
    }

    @Override
    public String getName() {
      return getClass().getName();
    }

    @Override
    public void prepare() {
      log.add( ACTIVATE_SCENE1 );
    }

    @Override
    public void close() {
      log.add( DEACTIVATE_SCENE1 );
    }
  }

  static class Scene2 implements Scene {

    private final List<Object> log;

    Scene2( List<Object> log ) {
      this.log = log;
    }

    @Override
    public String getName() {
      return getClass().getName();
    }

    @Override
    public void prepare() {
      log.add( ACTIVATE_SCENE2 );
    }

    @Override
    public void close() {
      log.add( DEACTIVATE_SCENE2 );
    }
  }

  @Before
  public void setUp() {
    log = new ArrayList<>();
    context = new TestContext();
    context.set( List.class, log );
    statusProvider = new MyStatusProvider();
    proxy = new DynamicSceneProxy<>( context, statusProvider, DynamicSceneProxyTest::determineSceneType );
  }

  @Test
  public void delegateToAppropriateSceneInstance() {
    statusProvider.setStatus( MyStatus.TWO );
    proxy.prepare();
    proxy.close();
    String firstDelegationName = proxy.getName();
    statusProvider.setStatus( MyStatus.ONE );
    proxy.prepare();
    proxy.close();
    String secondDelegationName = proxy.getName();

    assertThat( firstDelegationName ).isEqualTo( context.get( Scene2.class ).getName() );
    assertThat( secondDelegationName ).isEqualTo( context.get( Scene1.class ).getName() );
    assertThat( log )
      .containsExactly( ACTIVATE_SCENE2,
                        DEACTIVATE_SCENE2,
                        ACTIVATE_SCENE1,
                        DEACTIVATE_SCENE1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsContextArgument() {
    new DynamicSceneProxy<>( null, statusProvider, DynamicSceneProxyTest::determineSceneType );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsStatusProviderArgument() {
    new DynamicSceneProxy<>( context, null, DynamicSceneProxyTest::determineSceneType );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSceneProviderArgument() {
    new DynamicSceneProxy<>( context, statusProvider, null );
  }

  private static Class<? extends Scene> determineSceneType( MyStatus status ){
    return status == MyStatus.ONE ? Scene1.class : Scene2.class;
  }
}