package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.application.internal.zone.Messages.*;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.application.status.ZoneActivationProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;

public class ZoneActivationProviderImplTest {

  private static final EntityDefinition<?> ZONE_DEFINITION_1 = stubEntityDefinition( "Zone1" );
  private static final EntityDefinition<?> ZONE_DEFINITION_2 = stubEntityDefinition( "Zone2" );
  private static final EntityDefinition<?> ZONE_DEFINITION_3 = stubEntityDefinition( "Zone3" );
  private static final Entity<EntityDefinition<?>> ZONE_1 = stubEntity( ZONE_DEFINITION_1 );
  private static final Entity<EntityDefinition<?>> ZONE_2 = stubEntity( ZONE_DEFINITION_2 );
  private static final Entity<EntityDefinition<?>> ZONE_3 = stubEntity( ZONE_DEFINITION_3 );

  private ZoneActivationProviderImpl activation;
  private AdjacencyDefinition adjacency;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    adjacency = new AdjacencyDefinition( asSet( ZONE_DEFINITION_1, ZONE_DEFINITION_2, ZONE_DEFINITION_3 ) );
    adjacency.link( ZONE_DEFINITION_1, ZONE_DEFINITION_2 ).link( ZONE_DEFINITION_2, ZONE_DEFINITION_3 );
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    activation = new ZoneActivationProviderImpl( adjacency, eventBus, logger );
  }

  @Test
  public void engageZonesChanged() {
    activation.engagedZonesChanged( newEvent( ON, ZONE_1 ) );

    ArgumentCaptor<StatusEvent> captor = forClass( StatusEvent.class );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource( ZoneActivationProvider.class ) ).hasValue( activation );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1 ]" ) );
  }

  @Test
  public void getStatusAfterZoneEngaging() {
    Entity<EntityDefinition<?>> expected = ZONE_1;

    activation.engagedZonesChanged( newEvent( ON, expected ) );
    Set<ZoneActivation> actual = activation.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( expected ) );
    assertThat( actual.iterator().next().getReleaseTime() ).isEmpty();
  }

  @Test
  public void getStatusAfterSubsequentZoneEngaging() {
    activation.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    Set<ZoneActivation> actual = activation.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1, Zone2 ]" ) );
  }

  @Test
  public void getStatusAfterReleaseOfFirstEngaging() {
    Entity<EntityDefinition<?>> expected = ZONE_2;

    activation.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    activation.engagedZonesChanged( newEvent( ON, expected ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Set<ZoneActivation> actual = activation.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( expected ) );
  }

  @Test
  public void getStatusAtfterReleaseOfSecondEngaging() {
    Entity<EntityDefinition<?>> expected = ZONE_1;

    activation.engagedZonesChanged( newEvent( ON, expected ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Set<ZoneActivation> actual = activation.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( expected ) );
  }

  @Test
  public void getStatusAfterReleaseOfTheOnlyActiveZone() {
    Entity<EntityDefinition<?>> expected = ZONE_1;

    activation.engagedZonesChanged( newEvent( ON, expected ) );
    reset( logger, eventBus );
    activation.engagedZonesChanged( newEvent( OFF, expected ) );
    Set<ZoneActivation> actual = activation.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( expected ) );
    assertThat( actual.iterator().next().getReleaseTime() ).isNotEmpty();
    ArgumentCaptor<String> loggingCaptor = forClass( String.class );
    verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), loggingCaptor.capture() );
    assertThat( loggingCaptor.getValue() )
      .contains( ZONE_1.toString(), RELEASED_TAG, actual.iterator().next().getReleaseTime().get().toString() );
    ArgumentCaptor<StatusEvent> event = forClass( StatusEvent.class );
    verify( eventBus ).post( event.capture() );
    assertThat( event.getValue().getSource( ZoneActivationProvider.class ) ).hasValue( activation );
  }

  @Test
  public void getStatusAfterRemovalOfNonAdjacentZoneEngagings() {
    activation.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    Set<ZoneActivation> actual = activation.getStatus();

    ArgumentCaptor<String> captor = forClass( String.class );
    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_3 ) );
    InOrder order = inOrder( logger );
    order.verify( logger ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), eq( "[ Zone1 ]" ) );
    order.verify( logger, times( 2 ) ).info( eq( ZONE_ACTIVATION_STATUS_CHANGED_INFO ), captor.capture() );
    order.verifyNoMoreInteractions();
    assertThat( asList( captor.getValue().split( "\\|" ) ) )
      .allMatch( zone -> zone.contains( RELEASED_TAG ) && zone.contains( "Zone1" ) || zone.contains( "Zone3" ) )
      .hasSize( 2 );
  }

  @Test
  public void getStatusAfterMovementInNonAdjacentZoneEngagings() {
    activation.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    Set<ZoneActivation> actual = activation.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1, ZONE_2 ) );
  }

  @Test
  public void getStatusJoiningFromMultipleZoneEngagings() {
    activation.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_1, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_2 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_3 ) );
    activation.engagedZonesChanged( newEvent( ON, ZONE_1 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_2 ) );
    activation.engagedZonesChanged( newEvent( OFF, ZONE_1 ) );
    Set<ZoneActivation> actual = activation.getStatus();

    assertThat( toZoneSet( actual ) ).isEqualTo( $( ZONE_1 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAdjacencyDefinitionArgument() {
    new ZoneActivationProviderImpl( null, eventBus, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusDefinitionArgument() {
    new ZoneActivationProviderImpl( adjacency, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerDefinitionArgument() {
    new ZoneActivationProviderImpl( adjacency, eventBus, null );
  }

  @SafeVarargs
  private static Set<Entity<EntityDefinition<?>>> $( Entity<EntityDefinition<?>> ...zones ) {
    return asSet( zones );
  }

  @SafeVarargs
  private static <T> Set<T> asSet( T ... elements ) {
    return new HashSet<>( asList( elements ) );
  }

  @SafeVarargs
  @SuppressWarnings("unchecked")
  private static SensorEvent<OnOff> newEvent( OnOff sensorStatus, Entity<EntityDefinition<?>> ... affected ) {
    return new SensorEvent<>( mock( Entity.class ), sensorStatus, affected );
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

  private static Set<Entity<EntityDefinition<?>>> toZoneSet( Set<ZoneActivation> zoneActivations ) {
    return zoneActivations.stream().map( activation -> activation.getZone() ).collect( toSet() );
  }
}