package com.codeaffine.home.control.application.internal.activation;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.internal.activation.AdjacencyDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class AdjacencyDefinitionTest {

  private static final EntityDefinition<?> DEFINITION_1 = mock( EntityDefinition.class );
  private static final EntityDefinition<?> DEFINITION_2 = mock( EntityDefinition.class );
  private static final EntityDefinition<?> DEFINITION_3 = mock( EntityDefinition.class );
  private static final EntityDefinition<?> DEFINITION_4 = mock( EntityDefinition.class );

  private AdjacencyDefinition adjacencyDefinition;

  @Before
  public void setUp() {
    adjacencyDefinition = new AdjacencyDefinition( new HashSet<>( asList( DEFINITION_1, DEFINITION_2, DEFINITION_3 ) ) );
  }

  @Test
  public void initial() {
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_2 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_1 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_3 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_1 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_3 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_2 ) ).isFalse();
  }

  @Test
  public void link() {
    AdjacencyDefinition actual = adjacencyDefinition.link( DEFINITION_1, DEFINITION_2 );


    assertThat( actual ).isSameAs( adjacencyDefinition );
  }

  @Test
  public void linkPair() {
    adjacencyDefinition.link( DEFINITION_1, DEFINITION_2 );

    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_2 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_1 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_3 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_1 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_3 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_2 ) ).isFalse();
  }

  @Test
  public void linkTwoPairs() {
    adjacencyDefinition.link( DEFINITION_1, DEFINITION_2 );
    adjacencyDefinition.link( DEFINITION_2, DEFINITION_3 );

    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_2 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_1 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_3 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_1 ) ).isFalse();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_3 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_2 ) ).isTrue();
  }

  @Test
  public void linkAll() {
    adjacencyDefinition.link( DEFINITION_1, DEFINITION_2 );
    adjacencyDefinition.link( DEFINITION_2, DEFINITION_3 );
    adjacencyDefinition.link( DEFINITION_1, DEFINITION_3 );

    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_2 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_1 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_3 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_1 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_2, DEFINITION_3 ) ).isTrue();
    assertThat( adjacencyDefinition.isAdjacent( DEFINITION_3, DEFINITION_2 ) ).isTrue();
  }

  @Test( expected = IllegalArgumentException.class )
  public void linkWithNullAsFirstDefinition() {
    adjacencyDefinition.link( null, DEFINITION_1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void linkWithNullAsSecondDefinition() {
    adjacencyDefinition.link( null, DEFINITION_1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void linkWithDefintionAsFirstArgumentThatIsNoElementOfAdjacencyDefinition() {
    adjacencyDefinition.link( DEFINITION_4, DEFINITION_1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void linkWithDefintionAsSecondArgumentThatIsNoElementOfAdjacencyDefinition() {
    adjacencyDefinition.link( DEFINITION_1, DEFINITION_4 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAdjacentWithNullAsFirstDefinitionArgument() {
    adjacencyDefinition.isAdjacent( null, DEFINITION_1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAdjacentWithNullAsSecondDefinitionArgument() {
    adjacencyDefinition.isAdjacent( DEFINITION_1, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAdjacentWithDefintionAsFirstArgumentThatIsNoElementOfAdjacencyDefinition() {
    adjacencyDefinition.isAdjacent( DEFINITION_4, DEFINITION_1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAdjacentWithDefintionAsSecondArgumentThatIsNoElementOfAdjacencyDefinition() {
    adjacencyDefinition.isAdjacent( DEFINITION_1, DEFINITION_4 );
  }
}