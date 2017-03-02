package com.codeaffine.home.control.application.internal.zone;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.ZoneActivation;
import com.codeaffine.home.control.application.control.Event;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.ZoneEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

public class ZoneActivationImplTest {

  private static final EntityDefinition<?> ZONE_DEFINITION_1 = stubEntityDefinition( "Zone1" );
  private static final EntityDefinition<?> ZONE_DEFINITION_2 = stubEntityDefinition( "Zone2" );
  private static final EntityDefinition<?> ZONE_DEFINITION_3 = stubEntityDefinition( "Zone3" );
  private static final Entity<EntityDefinition<?>> ZONE_1 = stubEntity( ZONE_DEFINITION_1 );
  private static final Entity<EntityDefinition<?>> ZONE_2 = stubEntity( ZONE_DEFINITION_2 );
  private static final Entity<EntityDefinition<?>> ZONE_3 = stubEntity( ZONE_DEFINITION_3 );

  private AdjacencyDefinition adjacency;
  private ZoneActivationImpl activation;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    adjacency = new AdjacencyDefinition( asSet( ZONE_DEFINITION_1, ZONE_DEFINITION_2, ZONE_DEFINITION_3 ) );
    adjacency.link( ZONE_DEFINITION_1, ZONE_DEFINITION_2 ).link( ZONE_DEFINITION_2, ZONE_DEFINITION_3 );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    activation = new ZoneActivationImpl( adjacency, eventBus, logger );
  }

  @Test
  public void engageZonesChanged() {
    activation.engagedZonesChanged( newEvent( $( ZONE_1 ), $( ZONE_1 ), $() ) );

    ArgumentCaptor<Event> captor = forClass( Event.class );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource( ZoneActivation.class ) ).hasValue( activation );
  }

  @Test
    public void getStatusAfterZoneEngaging() {
      Set<Entity<EntityDefinition<?>>> expected = $( ZONE_1 );

      activation.engagedZonesChanged( newEvent( expected, expected, $() ) );
      Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

      assertThat( actual ).isEqualTo( expected );
    }

  @Test
  public void getStatusAfterSubsequentZoneEngaging() {
    Set<Entity<EntityDefinition<?>>> expected = $( ZONE_1, ZONE_2 );

    activation.engagedZonesChanged( newEvent( $( ZONE_1 ), $( ZONE_1 ), $() ) );
    activation.engagedZonesChanged( newEvent( expected, $( ZONE_2 ), $() ) );
    Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getStatusAfterReleaseOfFirstEngaging() {
    Set<Entity<EntityDefinition<?>>> expected = $( ZONE_2 );

    activation.engagedZonesChanged( newEvent( $( ZONE_1 ), $( ZONE_1 ), $() ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_1, ZONE_2 ), expected, $() ) );
    activation.engagedZonesChanged( newEvent( expected, $(), $( ZONE_1 ) ) );
    Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getStatusAtfterReleaseOfSecondEngaging() {
    Set<Entity<EntityDefinition<?>>> expected = $( ZONE_1 );

    activation.engagedZonesChanged( newEvent( expected, expected, $() ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_1, ZONE_2 ), $( ZONE_2 ), $() ) );
    activation.engagedZonesChanged( newEvent( expected, $(), $( ZONE_2 ) ) );
    Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getStatusAfterReleaseOfTheOnlyActiveZone() {
    Set<Entity<EntityDefinition<?>>> expected = $( ZONE_1 );

    activation.engagedZonesChanged( newEvent( expected, expected, $() ) );
    activation.engagedZonesChanged( newEvent( $(), $(), expected ) );
    Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getStatusAfterRemovalOfNonAdjacentZoneEngagings() {
    Set<Entity<EntityDefinition<?>>> expected = $( ZONE_1, ZONE_3 );

    activation.engagedZonesChanged( newEvent( $( ZONE_1 ), $( ZONE_1 ), $() ) );
    activation.engagedZonesChanged( newEvent( expected, $( ZONE_3 ), $() ) );
    activation.engagedZonesChanged( newEvent( $(), $(), expected ) );
    Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getStatusAfterMovementInNonAdjacentZoneEngagings() {
    activation.engagedZonesChanged( newEvent( $( ZONE_1 ), $( ZONE_1 ), $() ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_1, ZONE_3 ), $( ZONE_3 ), $() ) );
    activation.engagedZonesChanged( newEvent( $(), $(), $( ZONE_1, ZONE_3 ) ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_3 ), $( ZONE_3 ), $() ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_2 ), $( ZONE_2 ), $( ZONE_3 ) ) );
    activation.engagedZonesChanged( newEvent( $(), $(), $( ZONE_2 ) ) );
    Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

    assertThat( actual ).isEqualTo( $( ZONE_1, ZONE_2 ) );
  }

  @Test
  public void getStatusJoiningFromMultipleZoneEngagings() {
    activation.engagedZonesChanged( newEvent( $( ZONE_1 ), $( ZONE_1 ), $() ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_1, ZONE_3 ), $( ZONE_3 ), $() ) );
    activation.engagedZonesChanged( newEvent( $(), $(), $( ZONE_1, ZONE_3 ) ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_3 ), $( ZONE_3 ), $() ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_2 ), $( ZONE_2 ), $( ZONE_3 ) ) );
    activation.engagedZonesChanged( newEvent( $( ZONE_1 ), $( ZONE_1 ), $( ZONE_2 ) ) );
    activation.engagedZonesChanged( newEvent( $(), $(), $( ZONE_1 ) ) );
    Set<Entity<EntityDefinition<?>>> actual = activation.getStatus();

    assertThat( actual ).isEqualTo( $( ZONE_1 ) );
  }

  @SafeVarargs
  private static Set<Entity<EntityDefinition<?>>> $( Entity<EntityDefinition<?>> ...entities ) {
    return asSet( entities );
  }

  @SafeVarargs
  private static <T> Set<T> asSet( T ... elements ) {
    return new HashSet<>( asList( elements ) );
  }

  @SuppressWarnings("unchecked")
  private static ZoneEvent newEvent( Set<Entity<EntityDefinition<?>>> engagedZones,
                                     Set<Entity<EntityDefinition<?>>> additions,
                                     Set<Entity<EntityDefinition<?>>> removals )
  {
    return new ZoneEvent( mock( Entity.class ), engagedZones, additions, removals );
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static Entity<EntityDefinition<?>> stubEntity( EntityDefinition entityDefinition ) {
    Entity result = mock( Entity.class );
    when( result.getDefinition() ).thenReturn( entityDefinition );
    String toString = entityDefinition.toString();
    when( result.toString() ).thenReturn( toString );
    return result;
  }

  @SuppressWarnings("rawtypes")
  private static EntityDefinition stubEntityDefinition( String name) {
    EntityDefinition result = mock( EntityDefinition.class );
    when( result.toString() ).thenReturn( name );
    return result;
  }
}