
package com.codeaffine.home.control.internal.entity;
import static com.codeaffine.home.control.internal.entity.AllocationEventAssert.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class AllocationEventImplTest {

  private static final Entity<?> ALLOCATABLE_1 = mock( Entity.class );
  private static final Entity<?> ALLOCATABLE_2 = mock( Entity.class );
  private static final Entity<?> ALLOCATABLE_3 = mock( Entity.class );
  private static final Entity<?> ACTOR = mock( Entity.class );

  @Test
  public void accessors() {
    AllocationEventImpl actual
      = new AllocationEventImpl( ACTOR, asList( ALLOCATABLE_1 ), asList( ALLOCATABLE_2 ), asList( ALLOCATABLE_3 ) );

    assertThat( actual )
      .hasActor( ACTOR )
      .hasAllocations( ALLOCATABLE_1 )
      .hasAdditions( ALLOCATABLE_2 )
      .hasRemovals( ALLOCATABLE_3 );
  }

  @Test
  public void accessorsWithEmptyValues() {
    AllocationEventImpl actual = new AllocationEventImpl( ACTOR, emptyList(), emptyList(), emptyList() );

    assertThat( actual )
      .hasActor( ACTOR )
      .hasNoAllocations()
      .hasNoAdditions()
      .hasNoRemovals();
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsActorArgument() {
    new AllocationEventImpl( null, emptyList(), emptyList(), emptyList() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsAllocationsArgument() {
    new AllocationEventImpl( ACTOR, null, emptyList(), emptyList() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsAdditionsArgument() {
    new AllocationEventImpl( ACTOR, emptyList(), null, emptyList() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsRemovalsArgument() {
    new AllocationEventImpl( ACTOR, emptyList(), null, emptyList() );
  }
}
