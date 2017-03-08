package com.codeaffine.home.control.engine.status;

import static com.codeaffine.home.control.engine.status.Messages.*;
import static com.codeaffine.home.control.engine.status.SceneSelectorImpl.computeSelectedScenesInfo;
import static com.codeaffine.home.control.test.util.status.MyScope.*;
import static com.codeaffine.home.control.test.util.status.MyStatus.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.Scene;
import com.codeaffine.home.control.status.SceneSelector.Branch;
import com.codeaffine.home.control.status.SceneSelector.NodeCondition;
import com.codeaffine.home.control.status.SceneSelector.NodeDefinition;
import com.codeaffine.home.control.status.SceneSelector.Scope;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.home.control.test.util.status.MyStatus;
import com.codeaffine.home.control.test.util.status.MyStatusProvider;
import com.codeaffine.home.control.test.util.status.Scene1;
import com.codeaffine.home.control.test.util.status.Scene2;

public class SceneSelectorImplTest {

  private MyStatusProvider myStatusProvider;
  private SceneSelectorImpl sceneSelector;
  private Context context;
  private Logger logger;

  @Before
  public void setUp() {
    myStatusProvider = new MyStatusProvider();
    context = new TestContext();
    context.set( MyStatusProvider.class, myStatusProvider );
    logger = mock( Logger.class );
    sceneSelector = new SceneSelectorImpl( context, logger );
  }

  @Test
  public void selectWithMatchOfFirstCondition() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectSameSceneTwice() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> first = sceneSelector.select();
    Map<Scope, Scene> second = sceneSelector.select();

    assertThat( first )
      .isEqualTo( second )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( first ) );
  }

  @Test
  public void selectWithMatchOfSecondCondition() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseWhenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
      .thenSelect( Scene2.class );
    myStatusProvider.setStatus( TWO );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene2.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );

  }

  @Test
  public void selectWithMatchOfFallback() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseSelect( Scene2.class );
    myStatusProvider.setStatus( TWO );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene2.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectWithMatcthOfFirstNestedCondition() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
        .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectWithMatchOfSecondNestedCondition() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
        .thenSelect( Scene1.class )
      .otherwiseWhenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
        .thenSelect( Scene2.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene2.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectWithMatchOfNestedFallBack() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
        .thenSelect( Scene1.class )
      .otherwiseSelect( Scene2.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene2.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectWithMatchOfAndConjunctionExpression() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .and( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectWithMatchOfFirstOrConjunctionOperand() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .or( MyStatusProvider.class ).matches( status -> status == TWO )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );

  }

  @Test
  public void selectWithMatchOfSecondOrConjunctionOperand() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == TWO )
      .or( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectWithMatchOfThirdOrConjunctionOperand() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == TWO )
      .and( MyStatusProvider.class ).matches( status -> status == TWO )
      .or( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> actual = sceneSelector.select();

    assertThat( actual )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( actual ) );
  }

  @Test
  public void selectSceneASecondTimeAfterDefinitionChange() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( ONE );
    Map<Scope, Scene> first = sceneSelector.select();

    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == TWO )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( TWO );
    Map<Scope, Scene> second = sceneSelector.select();

    assertThat( first )
      .isEqualTo( second )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( first ) );
  }


  @Test
  public void selectSceneASecondTimeAfterStatusChange() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseSelect( Scene2.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> first = sceneSelector.select();
    myStatusProvider.setStatus( TWO );
    Map<Scope, Scene> second = sceneSelector.select();

    assertThat( first )
      .isNotEqualTo( second )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene1.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( first ) );
    assertThat( second )
      .isNotEqualTo( first )
      .hasSize( 1 )
      .containsKey( GLOBAL )
      .containsValue( context.get( Scene2.class ) );
    verify( logger ).info( INFO_SELECTED_SCENES, computeSelectedScenesInfo( second ) );
  }

  @Test
  public void validate() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual ).isNull();
  }

  @Test
  public void validateWithIncompleteFirstLevelDefinition() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual )
      .hasMessage( format( ERROR_INVALID_SCENE_SELECTION_CONFIGURATION_MISSING_OTHERWISE_SELECT, 1 ) )
      .isInstanceOf( IllegalStateException.class );
  }

  @Test
  public void validateWithValidNestedLevelDefinition() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
        .thenSelect( Scene1.class )
      .otherwiseSelect( Scene2.class )
    .otherwiseSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual ).isNull();
  }

  @Test
  public void validateWithIncompleteSecondLevelDefinition() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual )
     .hasMessage( format( ERROR_INVALID_SCENE_SELECTION_CONFIGURATION_MISSING_OTHERWISE_SELECT, 2 ) )
     .isInstanceOf( IllegalStateException.class );
  }

  @Test
  public void validateWithSuperfluousOtherwiseSelect() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseSelect( Scene1.class )
    .otherwiseSelect( Scene2.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual )
      .hasMessage( ERROR_SUPERFLUOUS_OTHERWISE_SELECT_BRANCH_DETECTED )
      .isInstanceOf( IllegalStateException.class );
  }

  @Test
  public void computeSelectedScenesInfoOnSelection() {
    sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    sceneSelector.whenStatusOf( LOCAL, MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene2.class );
    myStatusProvider.setStatus( ONE );

    Map<Scope, Scene> selection = sceneSelector.select();
    String actual = computeSelectedScenesInfo( selection );

    assertThat( actual )
      .contains( context.get( Scene1.class ).getName(),
                 GLOBAL.getName(),
                 context.get( Scene2.class ).getName(),
                 LOCAL.getName() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void whenStatusOfWithNullAsScopeArgument() {
    sceneSelector.whenStatusOf( null, MyStatusProvider.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void whenStatusOfWithNullAsStatusProviderTypeArgument() {
    sceneSelector.whenStatusOf( GLOBAL, null );
  }

  @Test
  public void matchesWithNullAsPredicateArgument() {
    NodeCondition<MyStatus> nodeCondition = sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class );

    Throwable actual = thrownBy( () -> nodeCondition.matches( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void thenSelectWithNullAsSceneArgument() {
    NodeDefinition nodeDefinition
      = sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == TWO );

    Throwable actual = thrownBy( () -> nodeDefinition.thenSelect( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void andWithNullAsStatusProviderTypeArgument() {
    NodeDefinition nodeDefinition
      = sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == TWO );

    Throwable actual = thrownBy( () -> nodeDefinition.and( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void orWithNullAsStatusProviderTypeArgument() {
    NodeDefinition nodeDefinition
      = sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == TWO );

    Throwable actual = thrownBy( () -> nodeDefinition.or( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void otherwiseWhenStatusOfWithNullAsStatusProviderTypeArgument() {
    Branch branch
      = sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
          .thenSelect( Scene1.class );

    Throwable actual = thrownBy( () -> branch.otherwiseWhenStatusOf( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void otherwiseSelectWithNullAsSceneArgument() {
    Branch branch
      = sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE )
          .thenSelect( Scene1.class );

    Throwable actual = thrownBy( () -> branch.otherwiseSelect( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void nestedWhenStatusOfWithNullAsStatusProviderTypeArgument() {
    NodeDefinition nodeDefinition
      = sceneSelector.whenStatusOf( GLOBAL, MyStatusProvider.class ).matches( status -> status == ONE );

    Throwable actual = thrownBy( () -> nodeDefinition.whenStatusOf( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }
}