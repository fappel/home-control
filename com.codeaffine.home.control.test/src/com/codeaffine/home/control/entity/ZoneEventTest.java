package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.internal.entity.ZoneEventAssert.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class ZoneEventTest {

  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> ZONE_1 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> ZONE_2 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> ZONE_3 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> SENSOR = mock( Entity.class );

  @Test
  public void accessors() {
    ZoneEvent actual = new ZoneEvent( SENSOR, asSet( ZONE_1 ), asSet( ZONE_2 ), asSet( ZONE_3 ) );

    assertThat( actual )
      .hasSensor( SENSOR )
      .hasEngagedZones( ZONE_1 )
      .hasAdditions( ZONE_2 )
      .hasRemovals( ZONE_3 );
  }

  @Test
  public void accessorsWithEmptyValues() {
    ZoneEvent actual = new ZoneEvent( SENSOR, emptySet(), emptySet(), emptySet() );

    assertThat( actual )
      .hasSensor( SENSOR )
      .hasNoEngagedZones()
      .hasNoAdditions()
      .hasNoRemovals();
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorArgument() {
    new ZoneEvent( null, emptySet(), emptySet(), emptySet() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsEngagedZonesArgument() {
    new ZoneEvent( SENSOR, null, emptySet(), emptySet() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsAdditionsArgument() {
    new ZoneEvent( SENSOR, emptySet(), null, emptySet() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsRemovalsArgument() {
    new ZoneEvent( SENSOR, emptySet(), null, emptySet() );
  }

  @SafeVarargs
  private static <T> Set<T> asSet( T ... elements ) {
    return new HashSet<>( asList( elements ) );
  }
}