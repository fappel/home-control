package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.test.util.entity.AllocationEventAssert.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class AllocationEventTest {

  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> ALLOCABLE_1 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> ALLOCABLE_2 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> ALLOCABLE_3 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> SENSOR = mock( Entity.class );

  @Test
  public void accessors() {
    AllocationEvent actual
      = new AllocationEvent( SENSOR, asSet( ALLOCABLE_1 ), asSet( ALLOCABLE_2 ), asSet( ALLOCABLE_3 ) );

    assertThat( actual )
      .hasSensor( SENSOR )
      .hasAllocated( ALLOCABLE_1 )
      .hasAdditions( ALLOCABLE_2 )
      .hasRemovals( ALLOCABLE_3 );
  }

  @Test
  public void accessorsWithEmptyValues() {
    AllocationEvent actual = new AllocationEvent( SENSOR, emptySet(), emptySet(), emptySet() );

    assertThat( actual )
      .hasSensor( SENSOR )
      .hasNoAllocated()
      .hasNoAdditions()
      .hasNoRemovals();
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorArgument() {
    new AllocationEvent( null, emptySet(), emptySet(), emptySet() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsAllocatedArgument() {
    new AllocationEvent( SENSOR, null, emptySet(), emptySet() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsAdditionsArgument() {
    new AllocationEvent( SENSOR, emptySet(), null, emptySet() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsRemovalsArgument() {
    new AllocationEvent( SENSOR, emptySet(), null, emptySet() );
  }

  @SafeVarargs
  private static <T> Set<T> asSet( T ... elements ) {
    return new HashSet<>( asList( elements ) );
  }
}