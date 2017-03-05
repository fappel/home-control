package com.codeaffine.home.control.application.internal.control;

import static com.codeaffine.home.control.application.internal.control.Messages.*;
import static com.codeaffine.home.control.application.internal.control.MyStatus.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.application.control.Scene;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.test.util.context.TestContext;

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
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene1.class );
    verify( logger ).info( INFO_SELECTED_SCENE, Scene1.class.getSimpleName() );
  }

  @Test
  public void selectSameSceneTwice() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
    .thenSelect( Scene1.class );
    Scene first = sceneSelector.select();
    Scene second = sceneSelector.select();

    assertThat( first )
      .isSameAs( second )
      .isInstanceOf( Scene1.class );
    verify( logger ).info( INFO_SELECTED_SCENE, Scene1.class.getSimpleName() );
  }

  @Test
  public void selectTwice() {
    myStatusProvider.setStatus( ONE );
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );

    Scene first = sceneSelector.select();
    Scene second = sceneSelector.select();

    assertThat( first )
      .isInstanceOf( Scene1.class )
      .isSameAs( second );
  }

  @Test
  public void selectWithMatchOfSecondCondition() {
    myStatusProvider.setStatus( TWO );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseWhenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
      .thenSelect( Scene2.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene2.class );
  }

  @Test
  public void selectWithMatchOfFallback() {
    myStatusProvider.setStatus( TWO );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseSelect( Scene2.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene2.class );
  }

  @Test
  public void selectWithMatchOfFirstNestedCondition() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
        .thenSelect( Scene1.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene1.class );
  }

  @Test
  public void selectWithMatchOfSecondNestedCondition() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
        .thenSelect( Scene1.class )
      .otherwiseWhenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
        .thenSelect( Scene2.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene2.class );
  }

  @Test
  public void selectWithMatchOfNestedFallBack() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
        .thenSelect( Scene1.class )
      .otherwiseSelect( Scene2.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene2.class );
  }

  @Test
  public void selectWithMatchOfAndConjunctionExpression() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .and( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene1.class );
  }

  @Test
  public void selectWithMatchOfFirstOrConjunctionOperand() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .or( MyStatusProvider.class ).matches( status -> status == TWO )
      .thenSelect( Scene1.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene1.class );
  }

  @Test
  public void selectWithMatchOfSecondOrConjunctionOperand() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
      .or( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene1.class );
  }

  @Test
  public void selectWithMatchOfThirdOrConjunctionOperand() {
    myStatusProvider.setStatus( ONE );

    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
      .and( MyStatusProvider.class ).matches( status -> status == TWO )
      .or( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    Scene actual = sceneSelector.select();

    assertThat( actual ).isInstanceOf( Scene1.class );
  }

  @Test
  public void selectSceneASecondTimeAfterDefinitionChange() {
    myStatusProvider.setStatus( ONE );
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );
    myStatusProvider.setStatus( TWO );
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
      .thenSelect( Scene1.class );

    Scene first = sceneSelector.select();
    Scene second = sceneSelector.select();

    assertThat( first )
      .isInstanceOf( Scene1.class )
      .isSameAs( second );
  }

  @Test
  public void validate() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual ).isNull();
  }

  @Test
  public void validateWithIncompleteFirstLevelDefinition() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual )
      .hasMessage( format( ERROR_INVALID_SCENE_SELECTION_CONFIGURATION_MISSING_OTHERWISE_SELECT, 1 ) )
      .isInstanceOf( IllegalStateException.class );
  }

  @Test
  public void validateWithValidNestedLevelDefinition() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
        .thenSelect( Scene1.class )
      .otherwiseSelect( Scene2.class )
    .otherwiseSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual ).isNull();
  }

  @Test
  public void validateWithIncompleteSecondLevelDefinition() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual )
     .hasMessage( format( ERROR_INVALID_SCENE_SELECTION_CONFIGURATION_MISSING_OTHERWISE_SELECT, 2 ) )
     .isInstanceOf( IllegalStateException.class );
  }

  @Test
  public void validateWithSuperfluousOtherwiseSelect() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .thenSelect( Scene1.class )
    .otherwiseSelect( Scene1.class )
    .otherwiseSelect( Scene2.class );

    Throwable actual = thrownBy( () -> sceneSelector.validate() );

    assertThat( actual )
      .hasMessage( ERROR_SUPERFLUOUS_OTHERWISE_SELECT_BRANCH_DETECTED )
      .isInstanceOf( IllegalStateException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void whenStatusOfWithNullAsStatusProviderTypeArgument() {
    sceneSelector.whenStatusOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void matchesWithNullAsPredicateArgument() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void thenSelectWithNullAsSceneArgument() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO ).thenSelect( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void andWithNullAsStatusProviderTypeArgument() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
      .and( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void orWithNullAsStatusProviderTypeArgument() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == TWO )
      .or( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void otherwiseWhenStatusOfWithNullAsStatusProviderTypeArgument() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE ).thenSelect( Scene1.class )
    .otherwiseWhenStatusOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void otherwiseSelectWithNullAsSceneArgument() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE ).thenSelect( Scene1.class )
      .otherwiseSelect( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void nestedWhenStatusOfWithNullAsStatusProviderTypeArgument() {
    sceneSelector.whenStatusOf( MyStatusProvider.class ).matches( status -> status == ONE )
      .whenStatusOf( null );
  }
}