
package com.codeaffine.home.control.internal.entity;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;

public class AllocationEventImplTest {

  private static final Entity<?> ENTITY_1 = mock( Entity.class );
  private static final Entity<?> ENTITY_2 = mock( Entity.class );
  private static final Entity<?> ENTITY_3 = mock( Entity.class );

  @Test
  public void accessors() {
    AllocationEventImpl event = new AllocationEventImpl( asList( ENTITY_1 ), ENTITY_2, ENTITY_3 );

    assertThat( event.getActual() ).hasSize( 1 ).contains( ENTITY_1 );
    assertThat( event.getAdded() ).contains( ENTITY_2 );
    assertThat( event.getRemoved() ).contains( ENTITY_3 );
  }

  @Test
  public void accessorsWithEmptyValues() {
    AllocationEventImpl event = new AllocationEventImpl( emptyList(), null, null );

    assertThat( event.getActual() ).isEmpty();
    assertThat( event.getAdded() ).isEmpty();
    assertThat( event.getRemoved() ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsActualArgument() {
    new AllocationEventImpl( null, null, null );
  }
}
